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

export type IncidentTab = 'UNASSIGNED' | 'ASSIGNED' | 'AWAITING_EVALUATION';

export const INCIDENT_TAB_LABELS: Record<IncidentTab, string> = {
  UNASSIGNED: 'Sans prestataire',
  ASSIGNED: 'En cours de résolution',
  AWAITING_EVALUATION: 'À évaluer',
};

export const EMPTY_TAB_MESSAGES: Record<IncidentTab, string> = {
  UNASSIGNED: "Aucun incident en attente d'affectation.",
  ASSIGNED: 'Aucun incident en cours de résolution.',
  AWAITING_EVALUATION: 'Toutes les interventions terminées ont été évaluées.',
};

export const NO_MATCHING_INCIDENT_MESSAGE = 'Aucun incident ne correspond à ces critères.';

export const ACTIVE_INCIDENT_LABELS = {
  '=0': 'Aucun incident actif',
  '=1': '1 incident actif',
  other: '# incidents actifs',
};

export const RED_ALERT_LABELS = {
  '=0': 'aucun en alerte rouge',
  other: '# en alerte rouge',
};

export const OUT_OF_SERVICE_LABELS = {
  '=0': 'Aucun équipement hors service',
  '=1': '1 équipement hors service',
  other: '# équipements hors service',
};

export type IncidentSortOrder = 'OLDEST_FIRST' | 'NEWEST_FIRST' | 'STATION';

export const INCIDENT_SORT_LABELS: Record<IncidentSortOrder, string> = {
  OLDEST_FIRST: "Plus anciens d'abord",
  NEWEST_FIRST: "Plus récents d'abord",
  STATION: 'Station (A → Z)',
};
