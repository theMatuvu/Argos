import { EquipmentType } from '../equipments/equipment.model';
import { Provider } from '../providers/provider.model';

export type IncidentStatus = 'UNASSIGNED' | 'ASSIGNED' | 'RESOLVED';

export type AttentionLevel = 'GREEN' | 'ORANGE' | 'RED' | 'ASSIGNED';

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
  attentionLevel: AttentionLevel | null;
  openedAt: string;
  resolvedAt: string | null;
  openDurationMinutes: number;
  rating: number | null;
  provider: Provider | null;
  providerUnderSurveillance: boolean;
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

export const ATTENTION_THRESHOLD_HOURS = {
  orange: 48,
  red: 72,
} as const;

export const ATTENTION_LEVEL_LABELS: Record<AttentionLevel, string> = {
  GREEN: `Moins de ${ATTENTION_THRESHOLD_HOURS.orange} h`,
  ORANGE: `De ${ATTENTION_THRESHOLD_HOURS.orange} h à ${ATTENTION_THRESHOLD_HOURS.red} h`,
  RED: `${ATTENTION_THRESHOLD_HOURS.red} h ou plus`,
  ASSIGNED: 'Pris en charge',
};

export const INCIDENT_DESCRIPTION_CONSTRAINTS = {
  minLength: 10,
  maxLength: 500,
} as const;

export const INCIDENT_DESCRIPTION_ERROR = `La description doit contenir entre ${INCIDENT_DESCRIPTION_CONSTRAINTS.minLength} et ${INCIDENT_DESCRIPTION_CONSTRAINTS.maxLength} caractères.`;
