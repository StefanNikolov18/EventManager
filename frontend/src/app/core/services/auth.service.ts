import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthenticationResponse } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = '/api/auth';

  private token = signal<string | null>(localStorage.getItem('token'));

  isLoggedIn = computed(() => !!this.token());

  login(request: LoginRequest): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(res => {
        localStorage.setItem('token', res.token);
        this.token.set(res.token);
      })
    );
  }

  register(request: RegisterRequest): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(res => {
        localStorage.setItem('token', res.token);
        this.token.set(res.token);
      })
    );
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/logout`, {}).subscribe({
      next: () => this.clearAuth(),
      error: () => this.clearAuth()
    });
  }

  getToken(): string | null {
    return this.token();
  }

  private clearAuth(): void {
    localStorage.removeItem('token');
    this.token.set(null);
    this.router.navigate(['/login']);
  }
}