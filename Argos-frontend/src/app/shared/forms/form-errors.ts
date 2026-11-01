import { AbstractControl } from '@angular/forms';

export function visibleError(control: AbstractControl, message: string): string | null {
  return control.touched && control.invalid ? message : null;
}
