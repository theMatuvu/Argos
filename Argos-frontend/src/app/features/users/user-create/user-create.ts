import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PROFILE_LABELS, ProfileType } from '../../../core/auth/auth.model';
import { apiErrorMessage } from '../../../core/http/api-error';
import { visibleError } from '../../../shared/forms/form-errors';
import { USER_CONSTRAINTS, User } from '../user.model';
import { UserService } from '../user.service';

type UserField = 'firstName' | 'lastName' | 'username' | 'password' | 'profile';

const FIELD_ERRORS: Record<UserField, string> = {
  firstName: `Le prénom est obligatoire (${USER_CONSTRAINTS.nameMaxLength} caractères maximum).`,
  lastName: `Le nom est obligatoire (${USER_CONSTRAINTS.nameMaxLength} caractères maximum).`,
  username: `L'identifiant est obligatoire (${USER_CONSTRAINTS.usernameMaxLength} caractères maximum).`,
  password: `Le mot de passe doit contenir entre ${USER_CONSTRAINTS.passwordMinLength} et ${USER_CONSTRAINTS.passwordMaxLength} caractères.`,
  profile: 'Le profil est obligatoire.',
};

const DEFAULT_PROFILE: ProfileType = 'FIELD_AGENT';

@Component({
  selector: 'app-user-create',
  imports: [ReactiveFormsModule],
  templateUrl: './user-create.html',
})
export class UserCreate {
  private readonly userService = inject(UserService);

  protected readonly profileLabels = PROFILE_LABELS;
  protected readonly profiles = Object.keys(PROFILE_LABELS) as ProfileType[];
  protected readonly form = inject(NonNullableFormBuilder).group({
    firstName: ['', [Validators.required, Validators.maxLength(USER_CONSTRAINTS.nameMaxLength)]],
    lastName: ['', [Validators.required, Validators.maxLength(USER_CONSTRAINTS.nameMaxLength)]],
    username: ['', [Validators.required, Validators.maxLength(USER_CONSTRAINTS.usernameMaxLength)]],
    password: [
      '',
      [
        Validators.required,
        Validators.minLength(USER_CONSTRAINTS.passwordMinLength),
        Validators.maxLength(USER_CONSTRAINTS.passwordMaxLength),
      ],
    ],
    profile: [DEFAULT_PROFILE, Validators.required],
  });
  protected readonly successMessage = signal<string | null>(null);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly submitting = signal(false);

  protected errorFor(field: UserField): string | null {
    return visibleError(this.form.controls[field], FIELD_ERRORS[field]);
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.userService.create(this.form.getRawValue()).subscribe({
      next: (user) => this.handleCreated(user),
      error: (error: HttpErrorResponse) => this.handleError(error),
    });
  }

  private handleCreated(user: User): void {
    this.successMessage.set(`Le compte « ${user.username} » a été créé.`);
    this.form.reset();
    this.submitting.set(false);
  }

  private handleError(error: HttpErrorResponse): void {
    this.errorMessage.set(apiErrorMessage(error));
    this.submitting.set(false);
  }
}
