import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { EventsPageComponent } from './features/events/events-page/events-page.component';

export const routes: Routes = [
  { path: '', redirectTo: '/events', pathMatch: 'full' },

  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },

  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },

  {
    path: 'events',
    canActivate: [authGuard],
    component: EventsPageComponent
  },

  { path: '**', redirectTo: '/events' }
];