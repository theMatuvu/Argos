import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { ProfileType } from './auth.model';
import { AuthService } from './auth.service';

export function roleGuard(profile: ProfileType): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    return auth
      .restoreSession()
      .pipe(map(() => auth.currentUser()?.profile === profile || router.createUrlTree(['/'])));
  };
}
