import type { LineSeriesOption } from 'echarts/charts';
import type { TooltipComponentOption } from 'echarts/components';
import { prefersReducedMotion } from '../../shared/motion';
import { ChartOption } from '../../shared/ui/chart/chart';
import { cssVariable } from '../../shared/ui/chart/css-variable';
import { AttentionLevel } from '../incidents/incident.model';
import { AttentionShare } from './statistics.model';

const ANIMATION_DURATION_MS = 1100;
const BAR_STAGGER_MS = 80;
const CURVE_SMOOTHNESS = 0.4;
const TOOLTIP_STYLE = 'border-radius: var(--radius-md); box-shadow: var(--shadow-md);';

export interface StatisticsPalette {
  primary: string;
  accent: string;
  navy: string;
  indigo: string;
  text: string;
  textMuted: string;
  border: string;
  surface: string;
  fontFamily: string;
  attention: Record<AttentionLevel, string>;
}

export interface TrendSeries {
  name: string;
  values: number[];
  color: string;
}

export interface RankingEntry {
  label: string;
  value: number;
}

export interface RankingStyle {
  color: string;
  formatValue: (value: number) => string;
  maxValue?: number;
}

export function readStatisticsPalette(): StatisticsPalette {
  return {
    primary: cssVariable('--color-primary'),
    accent: cssVariable('--color-accent'),
    navy: cssVariable('--color-brand-navy-light'),
    indigo: cssVariable('--color-attention-assigned'),
    text: cssVariable('--color-text'),
    textMuted: cssVariable('--color-text-muted'),
    border: cssVariable('--color-border'),
    surface: cssVariable('--color-surface'),
    fontFamily: cssVariable('--font-family'),
    attention: {
      GREEN: cssVariable('--color-attention-green'),
      ORANGE: cssVariable('--color-attention-orange'),
      RED: cssVariable('--color-attention-red'),
      ASSIGNED: cssVariable('--color-attention-assigned'),
    },
  };
}

export function trendChart(
  days: string[],
  series: TrendSeries[],
  palette: StatisticsPalette,
): ChartOption {
  return {
    ...baseOptions(palette),
    tooltip: tooltip(palette, {
      trigger: 'axis',
      axisPointer: { type: 'line', lineStyle: { color: palette.border } },
    }),
    legend: {
      top: 0,
      right: 0,
      icon: 'roundRect',
      itemWidth: 12,
      itemHeight: 4,
      itemGap: 20,
      textStyle: { color: palette.textMuted },
    },
    grid: { left: 0, right: 8, top: 40, bottom: 0, outerBoundsMode: 'same' },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: days,
      axisLine: { lineStyle: { color: palette.border } },
      axisTick: { show: false },
      axisLabel: { color: palette.textMuted, hideOverlap: true },
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: palette.border, type: 'dashed' } },
      axisLabel: { color: palette.textMuted },
    },
    series: series.map((trend) => areaSeries(trend, palette)),
  };
}

export function sparklineChart(
  values: number[],
  color: string,
  palette: StatisticsPalette,
): ChartOption {
  return {
    ...baseOptions(palette),
    grid: { left: 0, right: 0, top: 4, bottom: 0 },
    xAxis: { type: 'category', show: false, boundaryGap: false, data: values.map(String) },
    yAxis: { type: 'value', show: false, min: 0 },
    series: [
      {
        type: 'line',
        data: values,
        smooth: CURVE_SMOOTHNESS,
        symbol: 'none',
        lineStyle: { width: 2, color },
        areaStyle: { color: verticalFade(color) },
      },
    ],
  };
}

export function attentionChart(shares: AttentionShare[], palette: StatisticsPalette): ChartOption {
  return {
    ...baseOptions(palette),
    tooltip: tooltip(palette, { trigger: 'item', formatter: '{b} : <strong>{c}</strong>' }),
    series: [
      {
        type: 'pie',
        radius: ['70%', '92%'],
        padAngle: 2,
        itemStyle: { borderRadius: 6 },
        label: { show: false },
        emphasis: { scale: true, scaleSize: 5 },
        data: shares
          .filter((share) => share.count > 0)
          .map((share) => ({
            name: share.label,
            value: share.count,
            itemStyle: { color: share.color },
          })),
      },
    ],
  };
}

export function rankingChart(
  entries: RankingEntry[],
  style: RankingStyle,
  palette: StatisticsPalette,
): ChartOption {
  return {
    ...baseOptions(palette),
    tooltip: tooltip(palette, {
      trigger: 'axis',
      axisPointer: { type: 'shadow', shadowStyle: { color: withAlpha(style.color, 0.06) } },
      valueFormatter: (value) => style.formatValue(Number(value)),
    }),
    grid: { left: 0, right: 64, top: 0, bottom: 0, outerBoundsMode: 'same' },
    xAxis: { type: 'value', show: false, max: style.maxValue },
    yAxis: {
      type: 'category',
      inverse: true,
      data: entries.map((entry) => entry.label),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: palette.text, width: 150, overflow: 'truncate' },
    },
    series: [
      {
        type: 'bar',
        data: entries.map((entry) => entry.value),
        barWidth: 12,
        showBackground: true,
        backgroundStyle: { color: withAlpha(style.color, 0.08), borderRadius: 6 },
        itemStyle: {
          borderRadius: 6,
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: withAlpha(style.color, 0.55) },
              { offset: 1, color: style.color },
            ],
          },
        },
        label: {
          show: true,
          position: 'right',
          distance: 10,
          color: palette.text,
          fontWeight: 600,
          formatter: ({ value }) => style.formatValue(Number(value)),
        },
        animationDelay: (index) => index * BAR_STAGGER_MS,
      },
    ],
  };
}

function baseOptions(palette: StatisticsPalette): ChartOption {
  return {
    animation: !prefersReducedMotion(),
    animationDuration: ANIMATION_DURATION_MS,
    animationEasing: 'cubicOut',
    textStyle: { fontFamily: palette.fontFamily, color: palette.textMuted },
  };
}

function tooltip(
  palette: StatisticsPalette,
  options: TooltipComponentOption,
): TooltipComponentOption {
  return {
    backgroundColor: palette.surface,
    borderColor: palette.border,
    borderWidth: 1,
    padding: [8, 12],
    textStyle: { color: palette.text, fontFamily: palette.fontFamily, fontSize: 13 },
    extraCssText: TOOLTIP_STYLE,
    ...options,
  };
}

function areaSeries(trend: TrendSeries, palette: StatisticsPalette): LineSeriesOption {
  return {
    name: trend.name,
    type: 'line',
    data: trend.values,
    smooth: CURVE_SMOOTHNESS,
    showSymbol: false,
    symbol: 'circle',
    symbolSize: 8,
    lineStyle: { width: 2.5, color: trend.color },
    itemStyle: { color: trend.color, borderColor: palette.surface, borderWidth: 2 },
    areaStyle: { color: verticalFade(trend.color) },
    emphasis: { focus: 'series' },
  };
}

function verticalFade(color: string) {
  return {
    type: 'linear' as const,
    x: 0,
    y: 0,
    x2: 0,
    y2: 1,
    colorStops: [
      { offset: 0, color: withAlpha(color, 0.28) },
      { offset: 1, color: withAlpha(color, 0) },
    ],
  };
}

function withAlpha(hexColor: string, alpha: number): string {
  const [red, green, blue] = [1, 3, 5].map((start) =>
    parseInt(hexColor.slice(start, start + 2), 16),
  );
  return `rgba(${red}, ${green}, ${blue}, ${alpha})`;
}
