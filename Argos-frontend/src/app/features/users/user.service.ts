import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/http/api';
import { NewUser, User } from './user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);

  create(user: NewUser): Observable<User> {
    return this.http.post<User>(`${API_BASE_URL}/users`, user);
  }
}
