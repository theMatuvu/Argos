import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/http/api';
import { EquipmentDetails, StationEquipments, StationSummary } from './equipment.model';

@Injectable({ providedIn: 'root' })
export class EquipmentService {
  private readonly http = inject(HttpClient);

  getStations(): Observable<StationSummary[]> {
    return this.http.get<StationSummary[]>(`${API_BASE_URL}/stations`);
  }

  getStationEquipments(stationId: string): Observable<StationEquipments> {
    return this.http.get<StationEquipments>(`${API_BASE_URL}/stations/${stationId}/equipments`);
  }

  getEquipmentDetails(equipmentId: string): Observable<EquipmentDetails> {
    return this.http.get<EquipmentDetails>(`${API_BASE_URL}/equipments/${equipmentId}`);
  }
}
