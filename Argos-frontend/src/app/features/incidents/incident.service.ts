import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/http/api';
import { IncidentDetails } from './incident.model';

@Injectable({ providedIn: 'root' })
export class IncidentService {
  private readonly http = inject(HttpClient);

  declare(equipmentId: string, description: string): Observable<IncidentDetails> {
    return this.http.post<IncidentDetails>(`${API_BASE_URL}/equipments/${equipmentId}/incidents`, {
      description,
    });
  }

  getDetails(incidentId: string): Observable<IncidentDetails> {
    return this.http.get<IncidentDetails>(`${API_BASE_URL}/incidents/${incidentId}`);
  }

  getResolved(): Observable<IncidentDetails[]> {
    return this.http.get<IncidentDetails[]>(`${API_BASE_URL}/incidents/resolved`);
  }

  updateDescription(incidentId: string, description: string): Observable<IncidentDetails> {
    return this.http.patch<IncidentDetails>(`${API_BASE_URL}/incidents/${incidentId}`, {
      description,
    });
  }

  assignProvider(incidentId: string, providerId: string): Observable<IncidentDetails> {
    return this.http.put<IncidentDetails>(`${API_BASE_URL}/incidents/${incidentId}/provider`, {
      providerId,
    });
  }

  resolve(incidentId: string): Observable<IncidentDetails> {
    return this.http.post<IncidentDetails>(
      `${API_BASE_URL}/incidents/${incidentId}/resolution`,
      null,
    );
  }
}
