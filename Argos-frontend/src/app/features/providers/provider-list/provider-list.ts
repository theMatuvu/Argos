import { I18nPluralPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { apiErrorMessage } from '../../../core/http/api-error';
import { EVALUATION_COUNT_LABELS, SURVEILLANCE_LABEL } from '../provider.model';
import { ProviderService } from '../provider.service';
import { RatingStars } from '../rating-stars/rating-stars';

@Component({
  selector: 'app-provider-list',
  imports: [I18nPluralPipe, RatingStars, RouterLink],
  templateUrl: './provider-list.html',
})
export class ProviderList {
  private readonly providerService = inject(ProviderService);

  protected readonly evaluationCountLabels = EVALUATION_COUNT_LABELS;
  protected readonly surveillanceLabel = SURVEILLANCE_LABEL;
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly providers = toSignal(
    this.providerService.getProviders().pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorMessage.set(apiErrorMessage(error));
        return of(null);
      }),
    ),
    { initialValue: null },
  );
}
