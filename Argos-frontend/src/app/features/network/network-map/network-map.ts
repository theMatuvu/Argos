import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  DestroyRef,
  ElementRef,
  afterNextRender,
  inject,
  signal,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import * as L from 'leaflet';
import { apiErrorMessage } from '../../../core/http/api-error';
import { Network, NetworkLine, NetworkStation } from '../network.model';
import { NetworkService } from '../network.service';
import {
  STATIONS_PANE,
  groupLinesByStation,
  setLayerVisible,
  stationMarkerOptions,
  stationPopup,
  toLatLng,
} from './map-elements';

const PARIS_CENTER: L.LatLngTuple = [48.8566, 2.3522];
const INITIAL_ZOOM = 12;
const MAX_ZOOM = 19;
const LINE_WEIGHT = 6;
const FIT_PADDING: L.PointTuple = [24, 24];
const TILE_URL = 'https://tile.openstreetmap.org/{z}/{x}/{y}.png';
const TILE_ATTRIBUTION =
  '&copy; <a href="https://www.openstreetmap.org/copyright">les contributeurs d’OpenStreetMap</a> · ' +
  'Stations : Île-de-France Mobilités';

interface StationMarker {
  marker: L.CircleMarker;
  lines: NetworkLine[];
}

@Component({
  selector: 'app-network-map',
  templateUrl: './network-map.html',
  styleUrl: './network-map.css',
})
export class NetworkMap {
  private readonly networkService = inject(NetworkService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly mapContainer = viewChild.required<ElementRef<HTMLElement>>('map');

  protected readonly lines = signal<NetworkLine[]>([]);
  protected readonly hiddenLineIds = signal<ReadonlySet<string>>(new Set());
  protected readonly errorMessage = signal<string | null>(null);

  private leafletMap?: L.Map;
  private readonly lineLayers = new Map<string, L.Polyline>();
  private readonly stationMarkers: StationMarker[] = [];

  constructor() {
    afterNextRender(() => this.initialize());
    this.destroyRef.onDestroy(() => this.leafletMap?.remove());
  }

  protected isVisible(line: NetworkLine): boolean {
    return !this.hiddenLineIds().has(line.id);
  }

  protected toggleLine(line: NetworkLine): void {
    const hiddenLineIds = new Set(this.hiddenLineIds());
    if (hiddenLineIds.has(line.id)) {
      hiddenLineIds.delete(line.id);
    } else {
      hiddenLineIds.add(line.id);
    }
    this.hiddenLineIds.set(hiddenLineIds);
    this.refreshVisibility();
  }

  private initialize(): void {
    const leafletMap = L.map(this.mapContainer().nativeElement, {
      center: PARIS_CENTER,
      zoom: INITIAL_ZOOM,
    });
    L.tileLayer(TILE_URL, { attribution: TILE_ATTRIBUTION, maxZoom: MAX_ZOOM }).addTo(leafletMap);
    leafletMap.createPane(STATIONS_PANE);
    this.leafletMap = leafletMap;

    this.networkService
      .getNetwork()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (network) => this.draw(leafletMap, network),
        error: (error: HttpErrorResponse) => this.errorMessage.set(apiErrorMessage(error)),
      });
  }

  private draw(leafletMap: L.Map, network: Network): void {
    const stationsById = new Map(network.stations.map((station) => [station.id, station]));
    network.lines.forEach((line) => this.drawLine(leafletMap, line, stationsById));
    this.drawStations(leafletMap, network);
    this.fitToStations(leafletMap, network.stations);
    this.lines.set(network.lines);
  }

  private drawLine(
    leafletMap: L.Map,
    line: NetworkLine,
    stationsById: Map<string, NetworkStation>,
  ): void {
    const segments = line.segments.map((segment) => [
      toLatLng(stationsById.get(segment.fromStationId)!),
      toLatLng(stationsById.get(segment.toStationId)!),
    ]);
    const layer = L.polyline(segments, { color: line.color, weight: LINE_WEIGHT, opacity: 1 });
    this.lineLayers.set(line.id, layer.addTo(leafletMap));
  }

  private drawStations(leafletMap: L.Map, network: Network): void {
    const linesByStation = groupLinesByStation(network.lines);
    for (const station of network.stations) {
      const lines = linesByStation.get(station.id) ?? [];
      const marker = L.circleMarker(toLatLng(station), stationMarkerOptions(lines))
        .bindTooltip(station.name, { direction: 'top' })
        .bindPopup(() => stationPopup(station, lines, (selected) => this.showEquipments(selected)))
        .addTo(leafletMap);
      this.stationMarkers.push({ marker, lines });
    }
  }

  private showEquipments(station: NetworkStation): void {
    this.router.navigate(['/equipments'], { queryParams: { station: station.id } });
  }

  private fitToStations(leafletMap: L.Map, stations: NetworkStation[]): void {
    if (stations.length > 0) {
      leafletMap.fitBounds(L.latLngBounds(stations.map(toLatLng)), { padding: FIT_PADDING });
    }
  }

  private refreshVisibility(): void {
    const leafletMap = this.leafletMap;
    if (!leafletMap) {
      return;
    }
    const hiddenLineIds = this.hiddenLineIds();
    this.lineLayers.forEach((layer, lineId) =>
      setLayerVisible(leafletMap, layer, !hiddenLineIds.has(lineId)),
    );
    for (const { marker, lines } of this.stationMarkers) {
      const servedByVisibleLine = lines.some((line) => !hiddenLineIds.has(line.id));
      setLayerVisible(leafletMap, marker, lines.length === 0 || servedByVisibleLine);
    }
  }
}
