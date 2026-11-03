import { formatNumber } from '@angular/common';
import { Component, LOCALE_ID, computed, inject, input } from '@angular/core';
import { RATING_SCALE, RATING_VALUES } from '../provider.model';

const RATING_NUMBER_FORMAT = '1.0-1';

@Component({
  selector: 'app-rating-stars',
  templateUrl: './rating-stars.html',
  styleUrl: './rating-stars.css',
})
export class RatingStars {
  private readonly locale = inject(LOCALE_ID);

  readonly value = input.required<number>();

  protected readonly maxValue = RATING_SCALE.max;
  protected readonly stars = computed(() => {
    const filledStars = Math.round(this.value());
    return RATING_VALUES.map((star) => star <= filledStars);
  });
  protected readonly formattedValue = computed(() =>
    formatNumber(this.value(), this.locale, RATING_NUMBER_FORMAT),
  );
}
