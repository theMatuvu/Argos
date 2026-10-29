import { HttpClient, HttpErrorResponse, HttpParams, HttpStatusCode } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

type ProfileType = 'OPERATIONS_MANAGER' | 'FIELD_AGENT';

interface CurrentUser {
  firstName: string;
  profile: ProfileType;
}

@Component({
  selector: 'app-root',
  imports: [FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly http = inject(HttpClient);

  protected readonly profileLabels: Record<ProfileType, string> = {
    OPERATIONS_MANAGER: "Responsable d'exploitation",
    FIELD_AGENT: 'Agent de terrain'
  };
  protected readonly currentUser = signal<CurrentUser | null>(null);
  protected readonly message = signal('');
  protected credentials = { username: '', password: '' };
  protected newUser = { firstName: '', lastName: '', username: '', password: '', profile: 'FIELD_AGENT' };

  constructor() {
    this.loadCurrentUser();
  }

  protected login(): void {
    const body = new HttpParams()
      .set('username', this.credentials.username)
      .set('password', this.credentials.password);
    this.http.post('/api/auth/login', body).subscribe({
      next: () => {
        this.credentials.password = '';
        this.message.set('');
        this.loadCurrentUser();
      },
      error: (error: HttpErrorResponse) => this.message.set(this.describe(error))
    });
  }

  protected logout(): void {
    this.http.post('/api/auth/logout', null).subscribe({
      next: () => {
        this.currentUser.set(null);
        this.message.set('Vous êtes déconnecté.');
      },
      error: (error: HttpErrorResponse) => this.message.set(this.describe(error))
    });
  }

  protected createUser(): void {
    this.http.post<{ username: string }>('/api/users', this.newUser).subscribe({
      next: (user) => this.message.set(`Compte « ${user.username} » créé.`),
      error: (error: HttpErrorResponse) => this.message.set(this.describe(error))
    });
  }

  private loadCurrentUser(): void {
    this.http.get<CurrentUser>('/api/auth/me').subscribe({
      next: (user) => this.currentUser.set(user),
      error: () => this.currentUser.set(null)
    });
  }

  private describe(error: HttpErrorResponse): string {
    if (error.status === HttpStatusCode.Unauthorized) {
      return 'Identifiant ou mot de passe incorrect.';
    }
    if (error.status === HttpStatusCode.Forbidden) {
      return "Accès refusé : action réservée au responsable d'exploitation.";
    }
    return error.error?.detail ?? `Erreur ${error.status}`;
  }
}
