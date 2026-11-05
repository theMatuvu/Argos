import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { guestGuard } from './core/auth/guest.guard';
import { roleGuard } from './core/auth/role.guard';
import { Shell, WIDE_LAYOUT } from './core/layout/shell/shell';

export const routes: Routes = [
  {
    path: 'login',
    title: 'Connexion · Argos',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: '',
    component: Shell,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        title: 'Tableau de bord · Argos',
        data: WIDE_LAYOUT,
        loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard),
      },
      {
        path: 'statistics',
        title: 'Statistiques · Argos',
        loadComponent: () => import('./features/statistics/statistics').then((m) => m.Statistics),
      },
      {
        path: 'equipments',
        title: 'Équipements · Argos',
        loadComponent: () =>
          import('./features/equipments/equipment-list/equipment-list').then(
            (m) => m.EquipmentList,
          ),
      },
      {
        path: 'equipments/:equipmentId',
        title: 'Fiche équipement · Argos',
        loadComponent: () =>
          import('./features/equipments/equipment-detail/equipment-detail').then(
            (m) => m.EquipmentDetail,
          ),
      },
      {
        path: 'equipments/:equipmentId/declare-incident',
        title: 'Déclarer un incident · Argos',
        loadComponent: () =>
          import('./features/incidents/incident-declare/incident-declare').then(
            (m) => m.IncidentDeclare,
          ),
      },
      {
        path: 'incidents/:incidentId',
        title: "Détail de l'incident · Argos",
        loadComponent: () =>
          import('./features/incidents/incident-detail/incident-detail').then(
            (m) => m.IncidentDetail,
          ),
      },
      {
        path: 'history',
        title: 'Historique · Argos',
        loadComponent: () =>
          import('./features/incidents/incident-history/incident-history').then(
            (m) => m.IncidentHistory,
          ),
      },
      {
        path: 'providers',
        title: 'Prestataires · Argos',
        loadComponent: () =>
          import('./features/providers/provider-list/provider-list').then((m) => m.ProviderList),
      },
      {
        path: 'providers/:providerId',
        title: 'Fiche prestataire · Argos',
        loadComponent: () =>
          import('./features/providers/provider-detail/provider-detail').then(
            (m) => m.ProviderDetail,
          ),
      },
      {
        path: 'users',
        title: 'Comptes · Argos',
        canActivate: [roleGuard('OPERATIONS_MANAGER')],
        loadComponent: () =>
          import('./features/users/user-create/user-create').then((m) => m.UserCreate),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
