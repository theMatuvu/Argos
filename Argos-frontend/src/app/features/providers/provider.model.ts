import { IncidentDetails } from '../incidents/incident.model';

export interface Provider {
  id: string;
  name: string;
  contact: string;
}

export interface ProviderSummary extends Provider {
  averageRating: number | null;
  evaluationCount: number;
}

export interface ProviderDetails extends ProviderSummary {
  resolvedIncidents: IncidentDetails[];
}

export const RATING_SCALE = {
  min: 1,
  max: 5,
} as const;

export const RATING_VALUES: readonly number[] = Array.from(
  { length: RATING_SCALE.max - RATING_SCALE.min + 1 },
  (_, index) => RATING_SCALE.min + index,
);
