import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/http/api';
import { Provider } from './provider.model';

@Injectable({ providedIn: 'root' })
export class ProviderService {
  private readonly http = inject(HttpClient);

  getProviders(): Observable<Provider[]> {
    return this.http.get<Provider[]>(`${API_BASE_URL}/providers`);
  }
}
