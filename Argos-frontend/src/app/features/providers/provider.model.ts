import { IncidentDetails } from '../incidents/incident.model';

export interface Provider {
  id: string;
  name: string;
  contact: string;
}

export interface ProviderSummary extends Provider {
  averageRating: number | null;
  evaluationCount: number;
  recentAverageRating: number | null;
  underSurveillance: boolean;
}

export interface ProviderDetails extends ProviderSummary {
  resolvedIncidents: IncidentDetails[];
}

export const EVALUATION_COUNT_LABELS = {
  '=0': 'Aucune évaluation',
  '=1': '1 évaluation',
  other: '# évaluations',
};

export const SURVEILLANCE_LABEL = 'sous surveillance';

export const PROVIDER_UNDER_SURVEILLANCE_CODE = 'PROVIDER_UNDER_SURVEILLANCE';

export function providerOptionLabel(provider: ProviderSummary): string {
  return provider.underSurveillance ? `${provider.name} (${SURVEILLANCE_LABEL})` : provider.name;
}

export const RATING_SCALE = {
  min: 1,
  max: 5,
} as const;

export const RATING_VALUES: readonly number[] = Array.from(
  { length: RATING_SCALE.max - RATING_SCALE.min + 1 },
  (_, index) => RATING_SCALE.min + index,
);
