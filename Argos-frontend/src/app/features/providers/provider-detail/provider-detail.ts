import { DatePipe, I18nPluralPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, input, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { Observable, catchError, of, switchMap } from 'rxjs';
import { apiErrorMessage } from '../../../core/http/api-error';
import { DATE_TIME_FORMAT } from '../../../shared/date-format';
import { DurationPipe } from '../../../shared/duration.pipe';
import { ProviderDetails } from '../provider.model';
import { ProviderService } from '../provider.service';
import { RatingStars } from '../rating-stars/rating-stars';

const EVALUATION_COUNT_LABELS = {
  '=0': 'Aucune évaluation',
  '=1': 'Sur 1 évaluation',
  other: 'Sur # évaluations',
};

const RESOLVED_COUNT_LABELS = {
  '=0': 'Aucun incident résolu',
  '=1': '1 incident résolu',
  other: '# incidents résolus',
};

@Component({
  selector: 'app-provider-detail',
  imports: [DatePipe, DurationPipe, I18nPluralPipe, RatingStars, RouterLink],
  templateUrl: './provider-detail.html',
  styleUrl: './provider-detail.css',
})
export class ProviderDetail {
  private readonly providerService = inject(ProviderService);

  readonly providerId = input.required<string>();

  protected readonly dateTimeFormat = DATE_TIME_FORMAT;
  protected readonly evaluationCountLabels = EVALUATION_COUNT_LABELS;
  protected readonly resolvedCountLabels = RESOLVED_COUNT_LABELS;
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly details = toSignal(
    toObservable(this.providerId).pipe(switchMap((providerId) => this.load(providerId))),
    { initialValue: null },
  );

  private load(providerId: string): Observable<ProviderDetails | null> {
    this.errorMessage.set(null);
    return this.providerService.getProviderDetails(providerId).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorMessage.set(apiErrorMessage(error));
        return of(null);
      }),
    );
  }
}
