import { EquipmentType } from '../equipments/equipment.model';
import { AttentionLevel } from '../incidents/incident.model';
import { ProviderSummary } from '../providers/provider.model';

export interface StatisticsIndicators {
  activeIncidentCount: number;
  unassignedIncidentCount: number;
  openedIncidentCount: number;
  resolvedIncidentCount: number;
  averageResolutionMinutes: number | null;
  averageRating: number | null;
  evaluationCount: number;
}

export interface DailyIncidentActivity {
  day: string;
  openedCount: number;
  resolvedCount: number;
}

export interface EquipmentTypeStatistics {
  type: EquipmentType;
  incidentCount: number;
  averageResolutionMinutes: number | null;
}

export interface StationIncidentCount {
  stationName: string;
  incidentCount: number;
}

export interface NetworkStatistics {
  indicators: StatisticsIndicators;
  activeIncidentsByAttention: Record<AttentionLevel, number>;
  dailyActivity: DailyIncidentActivity[];
  equipmentTypes: EquipmentTypeStatistics[];
  mostAffectedStations: StationIncidentCount[];
}

export interface StatisticsReport {
  statistics: NetworkStatistics;
  providers: ProviderSummary[];
}

export interface AttentionShare {
  level: AttentionLevel;
  label: string;
  count: number;
  share: number;
  color: string;
}
