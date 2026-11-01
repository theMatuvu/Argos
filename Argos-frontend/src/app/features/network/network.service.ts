import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/http/api';
import { Network } from './network.model';

@Injectable({ providedIn: 'root' })
export class NetworkService {
  private readonly http = inject(HttpClient);

  getNetwork(): Observable<Network> {
    return this.http.get<Network>(`${API_BASE_URL}/network`);
  }
}
