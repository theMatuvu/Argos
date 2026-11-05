import {
  Component,
  DestroyRef,
  ElementRef,
  afterNextRender,
  effect,
  inject,
  input,
  signal,
  viewChild,
} from '@angular/core';
import { BarChart, LineChart, PieChart } from 'echarts/charts';
import type { BarSeriesOption, LineSeriesOption, PieSeriesOption } from 'echarts/charts';
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components';
import type {
  GridComponentOption,
  LegendComponentOption,
  TooltipComponentOption,
} from 'echarts/components';
import { init, use } from 'echarts/core';
import type { ComposeOption, EChartsType } from 'echarts/core';
import { SVGRenderer } from 'echarts/renderers';

use([BarChart, LineChart, PieChart, GridComponent, LegendComponent, TooltipComponent, SVGRenderer]);

export type ChartOption = ComposeOption<
  | BarSeriesOption
  | LineSeriesOption
  | PieSeriesOption
  | GridComponentOption
  | LegendComponentOption
  | TooltipComponentOption
>;

@Component({
  selector: 'app-chart',
  template: '<div #container class="chart"></div>',
  styles: `
    :host {
      display: block;
      height: var(--chart-height, 16rem);
    }

    .chart {
      width: 100%;
      height: 100%;
    }
  `,
})
export class Chart {
  readonly options = input.required<ChartOption>();

  private readonly container = viewChild.required<ElementRef<HTMLElement>>('container');
  private readonly instance = signal<EChartsType | null>(null);

  constructor() {
    const destroyRef = inject(DestroyRef);

    afterNextRender(() => {
      const element = this.container().nativeElement;
      const chart = init(element, null, { renderer: 'svg' });
      const resizeObserver = new ResizeObserver(() => chart.resize());
      resizeObserver.observe(element);
      this.instance.set(chart);

      destroyRef.onDestroy(() => {
        resizeObserver.disconnect();
        chart.dispose();
      });
    });

    effect(() => this.instance()?.setOption(this.options(), true));
  }
}
