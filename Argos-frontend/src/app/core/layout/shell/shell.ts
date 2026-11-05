import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRouteSnapshot, Data, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs';
import { Navbar } from '../navbar/navbar';

const WIDE_LAYOUT_KEY = 'wideLayout';

export const WIDE_LAYOUT: Data = { [WIDE_LAYOUT_KEY]: true };

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, Navbar],
  templateUrl: './shell.html',
  styleUrl: './shell.css',
  host: { '[class.wide-layout]': 'isWideLayout()' },
})
export class Shell {
  private readonly router = inject(Router);

  protected readonly isWideLayout = toSignal(
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map(() => this.currentRouteIsWide()),
    ),
    { initialValue: this.currentRouteIsWide() },
  );

  private currentRouteIsWide(): boolean {
    return deepestChild(this.router.routerState.snapshot.root).data[WIDE_LAYOUT_KEY] === true;
  }
}

function deepestChild(route: ActivatedRouteSnapshot): ActivatedRouteSnapshot {
  return route.firstChild ? deepestChild(route.firstChild) : route;
}
