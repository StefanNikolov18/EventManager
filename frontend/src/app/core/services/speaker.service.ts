import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SpeakerResponse, SpeakerRequest } from '../models/session.model';

@Injectable({ providedIn: 'root' })
export class SpeakerService {
  private http = inject(HttpClient);
  private apiUrl = '';

  getSpeakerById(id: number): Observable<SpeakerResponse> {
    return this.http.get<SpeakerResponse>(`${this.apiUrl}/speakers/${id}`);
  }

  updateSpeaker(id: number, request: SpeakerRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/speakers/${id}`, request);
  }

  deleteSpeaker(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/speakers/${id}`);
  }

  getSpeakersBySession(sessionId: number): Observable<SpeakerResponse[]> {
    return this.http.get<SpeakerResponse[]>(
      `${this.apiUrl}/speakers/sessions/${sessionId}/speakers`
    );
  }

  createSpeaker(sessionId: number, request: SpeakerRequest): Observable<SpeakerResponse> {
    return this.http.post<SpeakerResponse>(
      `${this.apiUrl}/speakers/sessions/${sessionId}/speakers`,
      request
    );
  }
}