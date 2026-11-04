import { IncidentDetails } from '../incidents/incident.model';

export interface DashboardIndicators {
  unassignedCount: number;
  assignedCount: number;
  awaitingEvaluationCount: number;
  outOfServiceEquipmentCount: number;
}

export interface Dashboard {
  indicators: DashboardIndicators;
  activeIncidents: IncidentDetails[];
  incidentsAwaitingEvaluation: IncidentDetails[];
}

export type IncidentSortOrder = 'OLDEST_FIRST' | 'NEWEST_FIRST' | 'STATION';

export const INCIDENT_SORT_LABELS: Record<IncidentSortOrder, string> = {
  OLDEST_FIRST: "Plus anciens d'abord",
  NEWEST_FIRST: "Plus récents d'abord",
  STATION: 'Station (A → Z)',
};
