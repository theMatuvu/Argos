import { DatePipe, I18nPluralPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { apiErrorMessage } from '../../core/http/api-error';
import { DATE_TIME_FORMAT } from '../../shared/date-format';
import { DurationPipe } from '../../shared/duration.pipe';
import { matchesSearch } from '../../shared/text-search';
import {
  ATTENTION_LEVEL_LABELS,
  AttentionLevel,
  INCIDENT_STATUS_LABELS,
  IncidentDetails,
} from '../incidents/incident.model';
import { NetworkMap } from '../network/network-map/network-map';
import {
  ACTIVE_INCIDENT_LABELS,
  EMPTY_TAB_MESSAGES,
  INCIDENT_SORT_LABELS,
  INCIDENT_TAB_LABELS,
  IncidentSortOrder,
  IncidentTab,
  NO_MATCHING_INCIDENT_MESSAGE,
  OUT_OF_SERVICE_LABELS,
  RED_ALERT_LABELS,
} from './dashboard.model';
import { DashboardService } from './dashboard.service';

type IncidentComparator = (first: IncidentDetails, second: IncidentDetails) => number;

const DEFAULT_SORT_ORDER: IncidentSortOrder = 'OLDEST_FIRST';
const DEFAULT_TAB: IncidentTab = 'UNASSIGNED';
const URGENCY_LEVELS: AttentionLevel[] = ['RED', 'ORANGE', 'GREEN'];

const referenceDate = (incident: IncidentDetails): string =>
  incident.resolvedAt ?? incident.openedAt;

const byReferenceDate: IncidentComparator = (first, second) =>
  referenceDate(first).localeCompare(referenceDate(second));

const INCIDENT_COMPARATORS: Record<IncidentSortOrder, IncidentComparator> = {
  OLDEST_FIRST: byReferenceDate,
  NEWEST_FIRST: (first, second) => byReferenceDate(second, first),
  STATION: (first, second) =>
    first.stationName.localeCompare(second.stationName, 'fr') || byReferenceDate(first, second),
};

@Component({
  selector: 'app-dashboard',
  imports: [DatePipe, DurationPipe, I18nPluralPipe, NetworkMap, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  private readonly dashboardService = inject(DashboardService);

  protected readonly dateTimeFormat = DATE_TIME_FORMAT;
  protected readonly activeIncidentLabels = ACTIVE_INCIDENT_LABELS;
  protected readonly redAlertLabels = RED_ALERT_LABELS;
  protected readonly attentionLabels = ATTENTION_LEVEL_LABELS;
  protected readonly urgencyLevels = URGENCY_LEVELS;
  protected readonly statusLabels = INCIDENT_STATUS_LABELS;
  protected readonly sortLabels = INCIDENT_SORT_LABELS;
  protected readonly sortOrders = Object.keys(INCIDENT_SORT_LABELS) as IncidentSortOrder[];
  protected readonly tabLabels = INCIDENT_TAB_LABELS;
  protected readonly tabs = Object.keys(INCIDENT_TAB_LABELS) as IncidentTab[];
  protected readonly outOfServiceLabels = OUT_OF_SERVICE_LABELS;

  protected readonly activeTab = signal<IncidentTab>(DEFAULT_TAB);
  protected readonly attentionFilter = signal<AttentionLevel | null>(null);
  protected readonly searchQuery = signal('');
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

  protected readonly incidentsByTab = computed<Record<IncidentTab, IncidentDetails[]>>(() => {
    const dashboard = this.dashboard();
    const activeIncidents = dashboard?.activeIncidents ?? [];
    return {
      UNASSIGNED: activeIncidents.filter((incident) => incident.status === 'UNASSIGNED'),
      ASSIGNED: activeIncidents.filter((incident) => incident.status === 'ASSIGNED'),
      AWAITING_EVALUATION: dashboard?.incidentsAwaitingEvaluation ?? [],
    };
  });

  protected readonly urgencyCounts = computed(() => {
    const unassignedIncidents = this.incidentsByTab().UNASSIGNED;
    return Object.fromEntries(
      URGENCY_LEVELS.map((level) => [
        level,
        unassignedIncidents.filter((incident) => incident.attentionLevel === level).length,
      ]),
    ) as Record<AttentionLevel, number>;
  });

  private readonly appliedAttentionFilter = computed(() =>
    this.activeTab() === 'UNASSIGNED' ? this.attentionFilter() : null,
  );

  protected readonly visibleIncidents = computed(() => {
    const level = this.appliedAttentionFilter();
    const query = this.searchQuery();
    const tabIncidents = this.incidentsByTab()[this.activeTab()];
    return tabIncidents
      .filter((incident) => !level || incident.attentionLevel === level)
      .filter((incident) =>
        matchesSearch(query, [
          incident.stationName,
          incident.equipmentLabel,
          incident.equipmentReference,
          incident.provider?.name,
        ]),
      )
      .sort(INCIDENT_COMPARATORS[this.sortOrder()]);
  });

  protected readonly emptyMessage = computed(() =>
    this.appliedAttentionFilter() || this.searchQuery().trim()
      ? NO_MATCHING_INCIDENT_MESSAGE
      : EMPTY_TAB_MESSAGES[this.activeTab()],
  );

  protected selectTab(tab: IncidentTab): void {
    this.activeTab.set(tab);
  }

  protected filterByAttention(level: AttentionLevel | null): void {
    this.attentionFilter.set(level);
  }

  protected search(query: string): void {
    this.searchQuery.set(query);
  }

  protected sortBy(order: string): void {
    this.sortOrder.set(order as IncidentSortOrder);
  }
}
