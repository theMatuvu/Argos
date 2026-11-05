import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input, linkedSignal, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable, catchError, of, switchMap } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { apiErrorMessage, hasApiErrorCode } from '../../../core/http/api-error';
import { DATE_TIME_FORMAT } from '../../../shared/date-format';
import { DurationPipe } from '../../../shared/duration.pipe';
import { visibleError } from '../../../shared/forms/form-errors';
import { EQUIPMENT_TYPE_LABELS } from '../../equipments/equipment.model';
import {
  PROVIDER_UNDER_SURVEILLANCE_CODE,
  RATING_SCALE,
  RATING_VALUES,
  SURVEILLANCE_LABEL,
  providerOptionLabel,
} from '../../providers/provider.model';
import { ProviderService } from '../../providers/provider.service';
import { RatingStars } from '../../providers/rating-stars/rating-stars';
import {
  ATTENTION_LEVEL_LABELS,
  INCIDENT_DESCRIPTION_CONSTRAINTS,
  INCIDENT_DESCRIPTION_ERROR,
  INCIDENT_STATUS_LABELS,
  IncidentDetails,
} from '../incident.model';
import { IncidentService } from '../incident.service';

const PROVIDER_REQUIRED_ERROR = 'Choisissez un prestataire.';
const RATING_REQUIRED_ERROR = 'Choisissez une note.';

@Component({
  selector: 'app-incident-detail',
  imports: [DatePipe, DurationPipe, RatingStars, ReactiveFormsModule, RouterLink],
  templateUrl: './incident-detail.html',
  styleUrl: './incident-detail.css',
})
export class IncidentDetail {
  private readonly incidentService = inject(IncidentService);
  private readonly providerService = inject(ProviderService);
  private readonly auth = inject(AuthService);
  private readonly formBuilder = inject(NonNullableFormBuilder);

  readonly incidentId = input.required<string>();

  protected readonly dateTimeFormat = DATE_TIME_FORMAT;
  protected readonly statusLabels = INCIDENT_STATUS_LABELS;
  protected readonly attentionLabels = ATTENTION_LEVEL_LABELS;
  protected readonly typeLabels = EQUIPMENT_TYPE_LABELS;
  protected readonly constraints = INCIDENT_DESCRIPTION_CONSTRAINTS;
  protected readonly ratingScale = RATING_SCALE;
  protected readonly ratingValues = RATING_VALUES;
  protected readonly surveillanceLabel = SURVEILLANCE_LABEL;
  protected readonly providerOptionLabel = providerOptionLabel;
  protected readonly isOperationsManager = computed(
    () => this.auth.currentUser()?.profile === 'OPERATIONS_MANAGER',
  );
  protected readonly loadError = signal<string | null>(null);
  protected readonly actionError = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly pending = signal(false);
  protected readonly editingDescription = signal(false);
  protected readonly confirmingResolution = signal(false);
  protected readonly surveillanceWarning = signal<string | null>(null);
  protected readonly providers = toSignal(
    this.providerService.getProviders().pipe(catchError(() => of([]))),
    { initialValue: [] },
  );
  protected readonly descriptionForm = this.formBuilder.group({
    description: [
      '',
      [
        Validators.required,
        Validators.minLength(INCIDENT_DESCRIPTION_CONSTRAINTS.minLength),
        Validators.maxLength(INCIDENT_DESCRIPTION_CONSTRAINTS.maxLength),
      ],
    ],
  });
  protected readonly assignmentForm = this.formBuilder.group({
    providerId: ['', Validators.required],
  });

  protected readonly evaluationForm = this.formBuilder.group({
    rating: this.formBuilder.control<number | null>(null, Validators.required),
  });

  private readonly loadedIncident = toSignal(
    toObservable(this.incidentId).pipe(switchMap((incidentId) => this.load(incidentId))),
    { initialValue: null },
  );
  protected readonly incident = linkedSignal(() => this.loadedIncident());

  constructor() {
    this.assignmentForm.controls.providerId.valueChanges
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.surveillanceWarning.set(null));
  }

  protected descriptionError(): string | null {
    return visibleError(this.descriptionForm.controls.description, INCIDENT_DESCRIPTION_ERROR);
  }

  protected providerError(): string | null {
    return visibleError(this.assignmentForm.controls.providerId, PROVIDER_REQUIRED_ERROR);
  }

  protected ratingError(): string | null {
    return visibleError(this.evaluationForm.controls.rating, RATING_REQUIRED_ERROR);
  }

  protected canBeEvaluated(incident: IncidentDetails): boolean {
    return incident.resolvedAt !== null && incident.provider !== null && incident.rating === null;
  }

  protected startEditing(incident: IncidentDetails): void {
    this.descriptionForm.reset({ description: incident.description });
    this.editingDescription.set(true);
  }

  protected cancelEditing(): void {
    this.editingDescription.set(false);
  }

  protected saveDescription(): void {
    if (this.descriptionForm.invalid) {
      this.descriptionForm.markAllAsTouched();
      return;
    }
    this.run(
      this.incidentService.updateDescription(
        this.incidentId(),
        this.descriptionForm.getRawValue().description,
      ),
      'La description a été mise à jour.',
    );
  }

  protected assignProvider(surveillanceConfirmed = false): void {
    if (this.assignmentForm.invalid) {
      this.assignmentForm.markAllAsTouched();
      return;
    }
    this.run(
      this.incidentService.assignProvider(
        this.incidentId(),
        this.assignmentForm.getRawValue().providerId,
        surveillanceConfirmed,
      ),
      "Le prestataire a été affecté à l'incident.",
    );
  }

  protected confirmAssignment(): void {
    this.assignProvider(true);
  }

  protected cancelAssignment(): void {
    this.surveillanceWarning.set(null);
  }

  protected askResolution(): void {
    this.confirmingResolution.set(true);
  }

  protected cancelResolution(): void {
    this.confirmingResolution.set(false);
  }

  protected resolve(): void {
    this.run(this.incidentService.resolve(this.incidentId()), "L'incident a été déclaré résolu.");
  }

  protected evaluate(): void {
    const { rating } = this.evaluationForm.getRawValue();
    if (this.evaluationForm.invalid || rating === null) {
      this.evaluationForm.markAllAsTouched();
      return;
    }
    this.run(
      this.incidentService.evaluate(this.incidentId(), rating),
      "L'évaluation de l'intervention a été enregistrée.",
    );
  }

  private run(request: Observable<IncidentDetails>, successMessage: string): void {
    this.pending.set(true);
    this.actionError.set(null);
    this.successMessage.set(null);
    this.surveillanceWarning.set(null);
    request.subscribe({
      next: (updated) => this.handleSuccess(updated, successMessage),
      error: (error: HttpErrorResponse) => this.handleActionError(error),
    });
  }

  private handleSuccess(updated: IncidentDetails, successMessage: string): void {
    this.incident.set(updated);
    this.successMessage.set(successMessage);
    this.editingDescription.set(false);
    this.confirmingResolution.set(false);
    this.assignmentForm.reset();
    this.evaluationForm.reset();
    this.pending.set(false);
  }

  private handleActionError(error: HttpErrorResponse): void {
    if (hasApiErrorCode(error, PROVIDER_UNDER_SURVEILLANCE_CODE)) {
      this.surveillanceWarning.set(apiErrorMessage(error));
    } else {
      this.actionError.set(apiErrorMessage(error));
    }
    this.confirmingResolution.set(false);
    this.pending.set(false);
  }

  private load(incidentId: string): Observable<IncidentDetails | null> {
    this.loadError.set(null);
    return this.incidentService.getDetails(incidentId).pipe(
      catchError((error: HttpErrorResponse) => {
        this.loadError.set(apiErrorMessage(error));
        return of(null);
      }),
    );
  }
}
