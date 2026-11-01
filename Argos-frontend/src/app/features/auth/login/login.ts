import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { apiErrorMessage } from '../../../core/http/api-error';
import { visibleError } from '../../../shared/forms/form-errors';
import { Logo } from '../../../shared/ui/logo/logo';

type LoginField = 'username' | 'password';

const FIELD_ERRORS: Record<LoginField, string> = {
  username: "L'identifiant est obligatoire.",
  password: 'Le mot de passe est obligatoire.',
};

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, Logo],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly form = inject(NonNullableFormBuilder).group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly submitting = signal(false);

  protected errorFor(field: LoginField): string | null {
    return visibleError(this.form.controls[field], FIELD_ERRORS[field]);
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { username, password } = this.form.getRawValue();
    this.submitting.set(true);
    this.errorMessage.set(null);
    this.auth.login(username, password).subscribe({
      next: () => this.router.navigate(['/']),
      error: (error: HttpErrorResponse) => this.handleError(error),
    });
  }

  private handleError(error: HttpErrorResponse): void {
    this.errorMessage.set(
      error.status === HttpStatusCode.Unauthorized
        ? 'Identifiant ou mot de passe incorrect.'
        : apiErrorMessage(error),
    );
    this.form.controls.password.reset();
    this.submitting.set(false);
  }
}
