import * as L from 'leaflet';
import { NetworkLine, NetworkStation } from '../network.model';

export const STATIONS_PANE = 'stations';

const STATION_RADIUS = 4;
const INTERCHANGE_RADIUS = 6;

export function toLatLng(station: NetworkStation): L.LatLngTuple {
  return [station.latitude, station.longitude];
}

export function groupLinesByStation(lines: NetworkLine[]): Map<string, NetworkLine[]> {
  const linesByStation = new Map<string, NetworkLine[]>();
  for (const line of lines) {
    for (const stationId of servedStationIds(line)) {
      linesByStation.set(stationId, [...(linesByStation.get(stationId) ?? []), line]);
    }
  }
  return linesByStation;
}

export function stationMarkerOptions(lines: NetworkLine[]): L.CircleMarkerOptions {
  if (lines.length === 1) {
    return {
      pane: STATIONS_PANE,
      radius: STATION_RADIUS,
      color: lines[0].color,
      className: 'station-marker',
    };
  }
  return {
    pane: STATIONS_PANE,
    radius: INTERCHANGE_RADIUS,
    className: 'station-marker station-marker-interchange',
  };
}

export function stationPopup(
  station: NetworkStation,
  lines: NetworkLine[],
  onShowEquipments: (station: NetworkStation) => void,
): HTMLElement {
  const name = document.createElement('strong');
  name.className = 'station-popup-name';
  name.textContent = station.name;

  const badges = document.createElement('div');
  badges.className = 'station-popup-lines';
  badges.append(...lines.map(lineBadge));

  const equipmentsLink = document.createElement('button');
  equipmentsLink.type = 'button';
  equipmentsLink.className = 'station-popup-link';
  equipmentsLink.textContent = 'Voir les équipements';
  equipmentsLink.addEventListener('click', () => onShowEquipments(station));

  const popup = document.createElement('div');
  popup.className = 'station-popup';
  popup.append(name, badges, equipmentsLink);
  return popup;
}

export function setLayerVisible(map: L.Map, layer: L.Layer, visible: boolean): void {
  if (visible) {
    map.addLayer(layer);
  } else {
    map.removeLayer(layer);
  }
}

function servedStationIds(line: NetworkLine): Set<string> {
  return new Set(line.segments.flatMap((segment) => [segment.fromStationId, segment.toStationId]));
}

function lineBadge(line: NetworkLine): HTMLElement {
  const badge = document.createElement('span');
  badge.className = 'line-badge';
  badge.style.backgroundColor = line.color;
  badge.style.color = line.textColor;
  badge.textContent = line.name;
  return badge;
}
