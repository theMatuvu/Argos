import { EquipmentType } from '../equipments/equipment.model';
import { Provider } from '../providers/provider.model';

export type IncidentStatus = 'UNASSIGNED' | 'ASSIGNED' | 'RESOLVED';

export interface Incident {
  id: string;
  description: string;
  openedAt: string;
  status: IncidentStatus;
  provider: Provider | null;
  resolvedAt: string | null;
  rating: number | null;
}

export interface IncidentDetails {
  id: string;
  description: string;
  status: IncidentStatus;
  openedAt: string;
  resolvedAt: string | null;
  openDurationMinutes: number;
  rating: number | null;
  provider: Provider | null;
  equipmentId: string;
  equipmentReference: string;
  equipmentLabel: string;
  equipmentType: EquipmentType;
  stationId: string;
  stationName: string;
}

export const INCIDENT_STATUS_LABELS: Record<IncidentStatus, string> = {
  UNASSIGNED: 'Non affecté',
  ASSIGNED: 'Affecté',
  RESOLVED: 'Résolu',
};

export const INCIDENT_DESCRIPTION_CONSTRAINTS = {
  minLength: 10,
  maxLength: 500,
} as const;

export const INCIDENT_DESCRIPTION_ERROR = `La description doit contenir entre ${INCIDENT_DESCRIPTION_CONSTRAINTS.minLength} et ${INCIDENT_DESCRIPTION_CONSTRAINTS.maxLength} caractères.`;
