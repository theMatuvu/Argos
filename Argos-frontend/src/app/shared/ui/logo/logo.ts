import { Component } from '@angular/core';

let nextLogoId = 0;

@Component({
  selector: 'app-logo',
  templateUrl: './logo.html',
  styleUrl: './logo.css',
})
export class Logo {
  protected readonly backgroundId = `argos-logo-background-${nextLogoId}`;
  protected readonly swooshId = `argos-logo-swoosh-${nextLogoId}`;
  protected readonly baselineId = `argos-logo-baseline-${nextLogoId++}`;

  protected reference(id: string): string {
    return `url(#${id})`;
  }
}
