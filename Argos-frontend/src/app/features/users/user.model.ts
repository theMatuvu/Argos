import { ProfileType } from '../../core/auth/auth.model';

export interface NewUser {
  firstName: string;
  lastName: string;
  username: string;
  password: string;
  profile: ProfileType;
}

export interface User {
  id: string;
  username: string;
  firstName: string;
  lastName: string;
  profile: ProfileType;
}

export const USER_CONSTRAINTS = {
  nameMaxLength: 100,
  usernameMaxLength: 50,
  passwordMinLength: 8,
  passwordMaxLength: 72,
} as const;
