import { I18nPluralPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';
import { Observable, catchError, of, switchMap } from 'rxjs';
import { apiErrorMessage } from '../../../core/http/api-error';
import {
  EQUIPMENT_TYPE_LABELS,
  OPERATING_STATUS_LABELS,
  StationEquipments,
  StationSummary,
} from '../equipment.model';
import { EquipmentService } from '../equipment.service';

const EQUIPMENT_COUNT_LABELS = {
  '=0': 'Aucun équipement',
  '=1': '1 équipement',
  other: '# équipements',
};

@Component({
  selector: 'app-equipment-list',
  imports: [I18nPluralPipe, RouterLink],
  templateUrl: './equipment-list.html',
  styleUrl: './equipment-list.css',
})
export class EquipmentList {
  private readonly equipmentService = inject(EquipmentService);
  private readonly router = inject(Router);

  readonly station = input<string>();

  protected readonly typeLabels = EQUIPMENT_TYPE_LABELS;
  protected readonly statusLabels = OPERATING_STATUS_LABELS;
  protected readonly equipmentCountLabels = EQUIPMENT_COUNT_LABELS;
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly stations = toSignal(this.loadStations(), { initialValue: [] });
  protected readonly stationEquipments = toSignal(
    toObservable(this.station).pipe(switchMap((stationId) => this.loadEquipments(stationId))),
    { initialValue: null },
  );
  protected readonly outOfServiceCount = computed(
    () =>
      this.stationEquipments()?.equipments.filter(
        (equipment) => equipment.operatingStatus === 'OUT_OF_SERVICE',
      ).length ?? 0,
  );

  protected selectStation(stationId: string): void {
    this.router.navigate(['/equipments'], { queryParams: { station: stationId || null } });
  }

  private loadStations(): Observable<StationSummary[]> {
    return this.equipmentService
      .getStations()
      .pipe(catchError((error: HttpErrorResponse) => this.showError(error, [])));
  }

  private loadEquipments(stationId: string | undefined): Observable<StationEquipments | null> {
    this.errorMessage.set(null);
    if (!stationId) {
      return of(null);
    }
    return this.equipmentService
      .getStationEquipments(stationId)
      .pipe(catchError((error: HttpErrorResponse) => this.showError(error, null)));
  }

  private showError<T>(error: HttpErrorResponse, fallback: T): Observable<T> {
    this.errorMessage.set(apiErrorMessage(error));
    return of(fallback);
  }
}
