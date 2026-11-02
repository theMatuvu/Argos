import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, input, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Observable, catchError, of, switchMap } from 'rxjs';
import { apiErrorMessage } from '../../../core/http/api-error';
import { visibleError } from '../../../shared/forms/form-errors';
import { EQUIPMENT_TYPE_LABELS, EquipmentDetails } from '../../equipments/equipment.model';
import { EquipmentService } from '../../equipments/equipment.service';
import { INCIDENT_DESCRIPTION_CONSTRAINTS, INCIDENT_DESCRIPTION_ERROR } from '../incident.model';
import { IncidentService } from '../incident.service';

@Component({
  selector: 'app-incident-declare',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './incident-declare.html',
})
export class IncidentDeclare {
  private readonly incidentService = inject(IncidentService);
  private readonly equipmentService = inject(EquipmentService);
  private readonly router = inject(Router);

  readonly equipmentId = input.required<string>();

  protected readonly constraints = INCIDENT_DESCRIPTION_CONSTRAINTS;
  protected readonly typeLabels = EQUIPMENT_TYPE_LABELS;
  protected readonly loadError = signal<string | null>(null);
  protected readonly submitError = signal<string | null>(null);
  protected readonly submitting = signal(false);
  protected readonly equipment = toSignal(
    toObservable(this.equipmentId).pipe(
      switchMap((equipmentId) => this.loadEquipment(equipmentId)),
    ),
    { initialValue: null },
  );
  protected readonly form = inject(NonNullableFormBuilder).group({
    description: [
      '',
      [
        Validators.required,
        Validators.minLength(INCIDENT_DESCRIPTION_CONSTRAINTS.minLength),
        Validators.maxLength(INCIDENT_DESCRIPTION_CONSTRAINTS.maxLength),
      ],
    ],
  });

  protected descriptionError(): string | null {
    return visibleError(this.form.controls.description, INCIDENT_DESCRIPTION_ERROR);
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);
    this.submitError.set(null);
    this.incidentService
      .declare(this.equipmentId(), this.form.getRawValue().description)
      .subscribe({
        next: (incident) => this.router.navigate(['/incidents', incident.id]),
        error: (error: HttpErrorResponse) => this.handleSubmitError(error),
      });
  }

  private handleSubmitError(error: HttpErrorResponse): void {
    this.submitError.set(apiErrorMessage(error));
    this.submitting.set(false);
  }

  private loadEquipment(equipmentId: string): Observable<EquipmentDetails | null> {
    this.loadError.set(null);
    return this.equipmentService.getEquipmentDetails(equipmentId).pipe(
      catchError((error: HttpErrorResponse) => {
        this.loadError.set(apiErrorMessage(error));
        return of(null);
      }),
    );
  }
}
