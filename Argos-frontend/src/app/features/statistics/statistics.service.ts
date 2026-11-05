import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/http/api';
import { NetworkStatistics } from './statistics.model';

@Injectable({ providedIn: 'root' })
export class StatisticsService {
  private readonly http = inject(HttpClient);

  getStatistics(): Observable<NetworkStatistics> {
    return this.http.get<NetworkStatistics>(`${API_BASE_URL}/statistics`);
  }
}
