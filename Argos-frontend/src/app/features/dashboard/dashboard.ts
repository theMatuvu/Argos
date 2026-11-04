import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { apiErrorMessage } from '../../core/http/api-error';
import { DATE_TIME_FORMAT } from '../../shared/date-format';
import { DurationPipe } from '../../shared/duration.pipe';
import {
  ATTENTION_LEVEL_LABELS,
  AttentionLevel,
  INCIDENT_STATUS_LABELS,
  IncidentDetails,
} from '../incidents/incident.model';
import { NetworkMap } from '../network/network-map/network-map';
import { INCIDENT_SORT_LABELS, IncidentSortOrder } from './dashboard.model';
import { DashboardService } from './dashboard.service';

type IncidentComparator = (first: IncidentDetails, second: IncidentDetails) => number;

const DEFAULT_SORT_ORDER: IncidentSortOrder = 'OLDEST_FIRST';

const byOpeningDate: IncidentComparator = (first, second) =>
  first.openedAt.localeCompare(second.openedAt);

const INCIDENT_COMPARATORS: Record<IncidentSortOrder, IncidentComparator> = {
  OLDEST_FIRST: byOpeningDate,
  NEWEST_FIRST: (first, second) => byOpeningDate(second, first),
  STATION: (first, second) =>
    first.stationName.localeCompare(second.stationName, 'fr') || byOpeningDate(first, second),
};

@Component({
  selector: 'app-dashboard',
  imports: [DatePipe, DurationPipe, NetworkMap, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  private readonly dashboardService = inject(DashboardService);

  protected readonly dateTimeFormat = DATE_TIME_FORMAT;
  protected readonly attentionLabels = ATTENTION_LEVEL_LABELS;
  protected readonly attentionLevels = Object.keys(ATTENTION_LEVEL_LABELS) as AttentionLevel[];
  protected readonly statusLabels = INCIDENT_STATUS_LABELS;
  protected readonly sortLabels = INCIDENT_SORT_LABELS;
  protected readonly sortOrders = Object.keys(INCIDENT_SORT_LABELS) as IncidentSortOrder[];

  protected readonly attentionFilter = signal<AttentionLevel | null>(null);
  protected readonly sortOrder = signal<IncidentSortOrder>(DEFAULT_SORT_ORDER);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly dashboard = toSignal(
    this.dashboardService.getDashboard().pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorMessage.set(apiErrorMessage(error));
        return of(null);
      }),
    ),
    { initialValue: null },
  );

  private readonly displayedIncidents = computed(() => {
    const level = this.attentionFilter();
    return (this.dashboard()?.activeIncidents ?? [])
      .filter((incident) => !level || incident.attentionLevel === level)
      .sort(INCIDENT_COMPARATORS[this.sortOrder()]);
  });

  protected readonly unassignedIncidents = computed(() =>
    this.displayedIncidents().filter((incident) => incident.status === 'UNASSIGNED'),
  );
  protected readonly assignedIncidents = computed(() =>
    this.displayedIncidents().filter((incident) => incident.status === 'ASSIGNED'),
  );

  protected filterByAttention(level: string): void {
    this.attentionFilter.set((level as AttentionLevel) || null);
  }

  protected sortBy(order: string): void {
    this.sortOrder.set(order as IncidentSortOrder);
  }
}
