import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, input, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { Observable, catchError, of, switchMap } from 'rxjs';
import { apiErrorMessage } from '../../../core/http/api-error';
import { DATE_TIME_FORMAT } from '../../../shared/date-format';
import { INCIDENT_STATUS_LABELS } from '../../incidents/incident.model';
import { RatingStars } from '../../providers/rating-stars/rating-stars';
import {
  EQUIPMENT_TYPE_LABELS,
  EquipmentDetails,
  OPERATING_STATUS_LABELS,
} from '../equipment.model';
import { EquipmentService } from '../equipment.service';

@Component({
  selector: 'app-equipment-detail',
  imports: [DatePipe, RatingStars, RouterLink],
  templateUrl: './equipment-detail.html',
  styleUrl: './equipment-detail.css',
})
export class EquipmentDetail {
  private readonly equipmentService = inject(EquipmentService);

  readonly equipmentId = input.required<string>();

  protected readonly dateTimeFormat = DATE_TIME_FORMAT;
  protected readonly typeLabels = EQUIPMENT_TYPE_LABELS;
  protected readonly operatingStatusLabels = OPERATING_STATUS_LABELS;
  protected readonly incidentStatusLabels = INCIDENT_STATUS_LABELS;
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly details = toSignal(
    toObservable(this.equipmentId).pipe(switchMap((equipmentId) => this.load(equipmentId))),
    { initialValue: null },
  );

  private load(equipmentId: string): Observable<EquipmentDetails | null> {
    this.errorMessage.set(null);
    return this.equipmentService.getEquipmentDetails(equipmentId).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorMessage.set(apiErrorMessage(error));
        return of(null);
      }),
    );
  }
}
