import { formatNumber } from '@angular/common';
import {
  Component,
  LOCALE_ID,
  computed,
  effect,
  inject,
  input,
  signal,
  untracked,
} from '@angular/core';
import { prefersReducedMotion } from '../../motion';

const ANIMATION_DURATION_MS = 1200;
const DEFAULT_DIGITS_FORMAT = '1.0-0';

const easeOutCubic = (progress: number): number => 1 - Math.pow(1 - progress, 3);

@Component({
  selector: 'app-animated-number',
  template: '{{ formattedValue() }}',
})
export class AnimatedNumber {
  private readonly locale = inject(LOCALE_ID);

  readonly value = input.required<number>();
  readonly digitsFormat = input(DEFAULT_DIGITS_FORMAT);

  private readonly displayedValue = signal(0);

  protected readonly formattedValue = computed(() =>
    formatNumber(this.displayedValue(), this.locale, this.digitsFormat()),
  );

  constructor() {
    effect((onCleanup) => {
      const target = this.value();
      if (prefersReducedMotion()) {
        this.displayedValue.set(target);
        return;
      }
      const origin = untracked(this.displayedValue);
      const startedAt = performance.now();
      let frame = 0;
      const step = (now: number) => {
        const progress = Math.min((now - startedAt) / ANIMATION_DURATION_MS, 1);
        this.displayedValue.set(origin + (target - origin) * easeOutCubic(progress));
        if (progress < 1) {
          frame = requestAnimationFrame(step);
        }
      };
      frame = requestAnimationFrame(step);
      onCleanup(() => cancelAnimationFrame(frame));
    });
  }
}
