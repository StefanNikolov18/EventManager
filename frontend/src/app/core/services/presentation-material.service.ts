import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PresentationMaterialResponse, PresentationMaterialRequest } from '../models/session.model';

@Injectable({ providedIn: 'root' })
export class PresentationMaterialService {
  private http = inject(HttpClient);
  private apiUrl = '';

  getMaterialsBySpeaker(speakerId: number): Observable<PresentationMaterialResponse[]> {
    return this.http.get<PresentationMaterialResponse[]>(
      `${this.apiUrl}/speakers/${speakerId}/materials`
    );
  }

  getMaterialById(id: number): Observable<PresentationMaterialResponse> {
    return this.http.get<PresentationMaterialResponse>(`${this.apiUrl}/materials/${id}`);
  }

  createMaterial(speakerId: number, request: PresentationMaterialRequest): Observable<PresentationMaterialResponse> {
    return this.http.post<PresentationMaterialResponse>(
      `${this.apiUrl}/speakers/${speakerId}/materials`,
      request
    );
  }

  deleteMaterial(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/materials/${id}`);
  }
}