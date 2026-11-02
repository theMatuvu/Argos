import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, catchError, map, of, switchMap, tap } from 'rxjs';
import { API_BASE_URL } from '../http/api';
import { CurrentUser } from './auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly user = signal<CurrentUser | null>(null);

  readonly currentUser = this.user.asReadonly();

  login(username: string, password: string): Observable<CurrentUser> {
    const credentials = new HttpParams().set('username', username).set('password', password);
    return this.http
      .post<void>(`${API_BASE_URL}/auth/login`, credentials)
      .pipe(switchMap(() => this.fetchCurrentUser()));
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${API_BASE_URL}/auth/logout`, null).pipe(tap(() => this.clear()));
  }

  restoreSession(): Observable<boolean> {
    if (this.user()) {
      return of(true);
    }
    return this.fetchCurrentUser().pipe(
      map(() => true),
      catchError(() => of(false)),
    );
  }

  clear(): void {
    this.user.set(null);
  }

  private fetchCurrentUser(): Observable<CurrentUser> {
    return this.http
      .get<CurrentUser>(`${API_BASE_URL}/auth/me`)
      .pipe(tap((user) => this.user.set(user)));
  }
}
