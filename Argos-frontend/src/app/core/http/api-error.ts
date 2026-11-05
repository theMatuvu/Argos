import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';

const UNREACHABLE_SERVER_STATUS = 0;

export function hasApiErrorCode(error: HttpErrorResponse, code: string): boolean {
  return error.error?.code === code;
}

export function apiErrorMessage(error: HttpErrorResponse): string {
  if (error.status === UNREACHABLE_SERVER_STATUS) {
    return 'Le serveur est injoignable. Vérifiez votre connexion puis réessayez.';
  }
  if (error.status === HttpStatusCode.Forbidden) {
    return "Vous n'avez pas les droits pour effectuer cette action.";
  }
  return error.error?.detail ?? 'Une erreur inattendue est survenue. Réessayez plus tard.';
}
