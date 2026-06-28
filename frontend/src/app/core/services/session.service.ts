import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SessionResponse, SessionRequest } from '../models/session.model';

@Injectable({ providedIn: 'root' })
export class SessionService {
  private http = inject(HttpClient);
  private apiUrl = '';

  getSessionsByEvent(eventId: number): Observable<SessionResponse[]> {
    return this.http.get<SessionResponse[]>(
      `${this.apiUrl}/sessions/events/${eventId}/sessions`
    );
  }

  getSessionById(id: number): Observable<SessionResponse> {
    return this.http.get<SessionResponse>(`${this.apiUrl}/sessions/${id}`);
  }

  createSession(eventId: number, request: SessionRequest): Observable<SessionResponse> {
    return this.http.post<SessionResponse>(
      `${this.apiUrl}/sessions/events/${eventId}/sessions`,
      request
    );
  }

  updateSession(id: number, request: SessionRequest): Observable<SessionResponse> {
    return this.http.put<SessionResponse>(
      `${this.apiUrl}/sessions/${id}`,
      request
    );
  }

  deleteSession(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/sessions/${id}`);
  }
}