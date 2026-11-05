import { I18nPluralPipe, PercentPipe, formatDate, formatNumber } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, LOCALE_ID, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, forkJoin, of } from 'rxjs';
import { apiErrorMessage } from '../../core/http/api-error';
import { AnimatedNumber } from '../../shared/ui/animated-number/animated-number';
import { Chart, ChartOption } from '../../shared/ui/chart/chart';
import { EQUIPMENT_TYPE_LABELS } from '../equipments/equipment.model';
import { ATTENTION_LEVEL_LABELS, AttentionLevel } from '../incidents/incident.model';
import {
  EVALUATION_COUNT_LABELS,
  ProviderSummary,
  RATING_SCALE,
} from '../providers/provider.model';
import { ProviderService } from '../providers/provider.service';
import {
  RankingEntry,
  RankingStyle,
  attentionChart,
  rankingChart,
  readStatisticsPalette,
  sparklineChart,
  trendChart,
} from './statistics-charts';
import { AttentionShare, NetworkStatistics, StatisticsReport } from './statistics.model';
import { StatisticsService } from './statistics.service';

interface RankingView {
  isEmpty: boolean;
  options: ChartOption;
}

const MINUTES_PER_HOUR = 60;
const DAY_LABEL_FORMAT = 'd MMM';
const COUNT_FORMAT = '1.0-0';
const HOURS_FORMAT = '1.0-1';
const RATING_FORMAT = '1.1-1';
const ATTENTION_LEVELS = Object.keys(ATTENTION_LEVEL_LABELS) as AttentionLevel[];

const toHours = (minutes: number): number => minutes / MINUTES_PER_HOUR;
const byDescendingValue = (first: RankingEntry, second: RankingEntry): number =>
  second.value - first.value;

@Component({
  selector: 'app-statistics',
  imports: [AnimatedNumber, Chart, I18nPluralPipe, PercentPipe],
  templateUrl: './statistics.html',
  styleUrl: './statistics.css',
})
export class Statistics {
  private readonly statisticsService = inject(StatisticsService);
  private readonly providerService = inject(ProviderService);
  private readonly locale = inject(LOCALE_ID);
  private readonly palette = readStatisticsPalette();

  protected readonly evaluationCountLabels = EVALUATION_COUNT_LABELS;
  protected readonly hoursFormat = HOURS_FORMAT;
  protected readonly ratingFormat = RATING_FORMAT;
  protected readonly maxRating = RATING_SCALE.max;
  protected readonly errorMessage = signal<string | null>(null);

  private readonly report = toSignal(
    forkJoin({
      statistics: this.statisticsService.getStatistics(),
      providers: this.providerService.getProviders(),
    }).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorMessage.set(apiErrorMessage(error));
        return of(null);
      }),
    ),
    { initialValue: null },
  );

  protected readonly view = computed(() => {
    const report = this.report();
    return report && this.buildView(report);
  });

  private buildView({ statistics, providers }: StatisticsReport) {
    const { indicators, dailyActivity } = statistics;
    const days = dailyActivity.map((day) => formatDate(day.day, DAY_LABEL_FORMAT, this.locale));
    const openedCounts = dailyActivity.map((day) => day.openedCount);
    const resolvedCounts = dailyActivity.map((day) => day.resolvedCount);
    const attentionShares = this.attentionShares(statistics);
    return {
      indicators,
      attentionShares,
      periodDays: dailyActivity.length,
      averageResolutionHours:
        indicators.averageResolutionMinutes === null
          ? null
          : toHours(indicators.averageResolutionMinutes),
      openedSparkline: sparklineChart(openedCounts, this.palette.accent, this.palette),
      activity: trendChart(
        days,
        [
          { name: 'Incidents ouverts', values: openedCounts, color: this.palette.accent },
          { name: 'Incidents résolus', values: resolvedCounts, color: this.palette.primary },
        ],
        this.palette,
      ),
      attention: attentionChart(attentionShares, this.palette),
      incidentsByType: this.ranking(this.incidentsByType(statistics), {
        color: this.palette.primary,
        formatValue: (value) => this.formatCount(value),
      }),
      resolutionByType: this.ranking(this.resolutionByType(statistics), {
        color: this.palette.accent,
        formatValue: (value) => `${formatNumber(value, this.locale, HOURS_FORMAT)} h`,
      }),
      stations: this.ranking(this.incidentsByStation(statistics), {
        color: this.palette.navy,
        formatValue: (value) => this.formatCount(value),
      }),
      providers: this.ranking(this.ratingsByProvider(providers), {
        color: this.palette.indigo,
        formatValue: (value) =>
          `${formatNumber(value, this.locale, RATING_FORMAT)} / ${RATING_SCALE.max}`,
        maxValue: RATING_SCALE.max,
      }),
    };
  }

  private attentionShares({
    activeIncidentsByAttention,
    indicators,
  }: NetworkStatistics): AttentionShare[] {
    const total = indicators.activeIncidentCount;
    return ATTENTION_LEVELS.map((level) => {
      const count = activeIncidentsByAttention[level];
      return {
        level,
        count,
        label: ATTENTION_LEVEL_LABELS[level],
        share: total > 0 ? count / total : 0,
        color: this.palette.attention[level],
      };
    });
  }

  private incidentsByType({ equipmentTypes }: NetworkStatistics): RankingEntry[] {
    return equipmentTypes
      .map(({ type, incidentCount }) => ({
        label: EQUIPMENT_TYPE_LABELS[type],
        value: incidentCount,
      }))
      .sort(byDescendingValue);
  }

  private resolutionByType({ equipmentTypes }: NetworkStatistics): RankingEntry[] {
    return equipmentTypes
      .flatMap(({ type, averageResolutionMinutes }) =>
        averageResolutionMinutes === null
          ? []
          : [{ label: EQUIPMENT_TYPE_LABELS[type], value: toHours(averageResolutionMinutes) }],
      )
      .sort(byDescendingValue);
  }

  private incidentsByStation({ mostAffectedStations }: NetworkStatistics): RankingEntry[] {
    return mostAffectedStations.map(({ stationName, incidentCount }) => ({
      label: stationName,
      value: incidentCount,
    }));
  }

  private ratingsByProvider(providers: ProviderSummary[]): RankingEntry[] {
    return providers
      .flatMap(({ name, averageRating }) =>
        averageRating === null ? [] : [{ label: name, value: averageRating }],
      )
      .sort(byDescendingValue);
  }

  private ranking(entries: RankingEntry[], style: RankingStyle): RankingView {
    return { isEmpty: entries.length === 0, options: rankingChart(entries, style, this.palette) };
  }

  private formatCount(value: number): string {
    return formatNumber(value, this.locale, COUNT_FORMAT);
  }
}
