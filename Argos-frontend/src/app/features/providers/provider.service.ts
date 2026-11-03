import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/http/api';
import { ProviderDetails, ProviderSummary } from './provider.model';

@Injectable({ providedIn: 'root' })
export class ProviderService {
  private readonly http = inject(HttpClient);

  getProviders(): Observable<ProviderSummary[]> {
    return this.http.get<ProviderSummary[]>(`${API_BASE_URL}/providers`);
  }

  getProviderDetails(providerId: string): Observable<ProviderDetails> {
    return this.http.get<ProviderDetails>(`${API_BASE_URL}/providers/${providerId}`);
  }
}
