export type IncidentStatus = 'UNASSIGNED' | 'ASSIGNED' | 'RESOLVED';

export interface Provider {
  id: string;
  name: string;
  contact: string;
}

export interface Incident {
  id: string;
  description: string;
  openedAt: string;
  status: IncidentStatus;
  provider: Provider | null;
  resolvedAt: string | null;
}

export const INCIDENT_STATUS_LABELS: Record<IncidentStatus, string> = {
  UNASSIGNED: 'Non affecté',
  ASSIGNED: 'Affecté',
  RESOLVED: 'Résolu',
};
