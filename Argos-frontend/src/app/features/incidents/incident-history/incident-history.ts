import { DatePipe, I18nPluralPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { apiErrorMessage } from '../../../core/http/api-error';
import { DATE_TIME_FORMAT } from '../../../shared/date-format';
import { DurationPipe } from '../../../shared/duration.pipe';
import { RatingStars } from '../../providers/rating-stars/rating-stars';
import { IncidentService } from '../incident.service';

const RESOLVED_COUNT_LABELS = {
  '=0': 'Aucun incident résolu',
  '=1': '1 incident résolu',
  other: '# incidents résolus',
};

@Component({
  selector: 'app-incident-history',
  imports: [DatePipe, DurationPipe, I18nPluralPipe, RatingStars, RouterLink],
  templateUrl: './incident-history.html',
  styleUrl: './incident-history.css',
})
export class IncidentHistory {
  private readonly incidentService = inject(IncidentService);

  protected readonly dateTimeFormat = DATE_TIME_FORMAT;
  protected readonly countLabels = RESOLVED_COUNT_LABELS;
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly incidents = toSignal(
    this.incidentService.getResolved().pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorMessage.set(apiErrorMessage(error));
        return of(null);
      }),
    ),
    { initialValue: null },
  );
}
