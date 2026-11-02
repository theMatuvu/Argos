import { Incident } from '../incidents/incident.model';

export type EquipmentType =
  'ELEVATOR' | 'ESCALATOR' | 'TICKET_GATE' | 'TICKET_VENDING_MACHINE' | 'INFORMATION_DISPLAY';

export type OperatingStatus = 'IN_SERVICE' | 'OUT_OF_SERVICE';

export interface StationSummary {
  id: string;
  name: string;
}

export interface Equipment {
  id: string;
  reference: string;
  label: string;
  type: EquipmentType;
  operatingStatus: OperatingStatus;
}

export interface StationEquipments {
  station: StationSummary;
  equipments: Equipment[];
}

export interface EquipmentDetails extends Equipment {
  stationId: string;
  stationName: string;
  activeIncidents: Incident[];
  pastIncidents: Incident[];
}

export const EQUIPMENT_TYPE_LABELS: Record<EquipmentType, string> = {
  ELEVATOR: 'Ascenseur',
  ESCALATOR: 'Escalier mécanique',
  TICKET_GATE: 'Portiques de validation',
  TICKET_VENDING_MACHINE: 'Distributeur de titres',
  INFORMATION_DISPLAY: "Écran d'information",
};

export const OPERATING_STATUS_LABELS: Record<OperatingStatus, string> = {
  IN_SERVICE: 'En service',
  OUT_OF_SERVICE: 'Hors service',
};
