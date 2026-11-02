import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Logo } from '../../../shared/ui/logo/logo';
import { PROFILE_LABELS, ProfileType } from '../../auth/auth.model';
import { AuthService } from '../../auth/auth.service';

interface NavLink {
  path: string;
  label: string;
  requiredProfile?: ProfileType;
}

const NAV_LINKS: NavLink[] = [
  { path: '/dashboard', label: 'Tableau de bord' },
  { path: '/equipments', label: 'Équipements' },
  { path: '/history', label: 'Historique' },
  { path: '/users', label: 'Comptes', requiredProfile: 'OPERATIONS_MANAGER' },
];

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive, Logo],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly currentUser = this.auth.currentUser;
  protected readonly profileLabels = PROFILE_LABELS;
  protected readonly links = computed(() =>
    NAV_LINKS.filter(
      (link) => !link.requiredProfile || link.requiredProfile === this.currentUser()?.profile,
    ),
  );

  protected logout(): void {
    this.auth.logout().subscribe(() => this.router.navigate(['/login']));
  }
}
