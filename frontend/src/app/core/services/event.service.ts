import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventResponse, EventRequest, PageResponse, Category } from '../models/event.model';

@Injectable({ providedIn: 'root' })
export class EventService {
  private http = inject(HttpClient);
  private apiUrl = '';

  getEventsPage(page: number, size: number, categoryId?: number): Observable<PageResponse<EventResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (categoryId != null) {
      params = params.set('categoryId', categoryId.toString());
    }

    return this.http.get<PageResponse<EventResponse>>(`${this.apiUrl}/events/page`, { params });
  }

  getEvents(title?: string, venue?: string, categoryId?: number): Observable<EventResponse[]> {
    let params = new HttpParams();
    if (title) params = params.set('title', title);
    if (venue) params = params.set('venue', venue);
    if (categoryId != null) params = params.set('categoryId', categoryId.toString());

    return this.http.get<EventResponse[]>(`${this.apiUrl}/events`, { params });
  }

  getEventById(id: number): Observable<EventResponse> {
    return this.http.get<EventResponse>(`${this.apiUrl}/events/${id}`);
  }

  createEvent(request: EventRequest): Observable<EventResponse> {
    return this.http.post<EventResponse>(`${this.apiUrl}/events`, request);
  }

  updateEvent(id: number, request: EventRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/events/${id}`, request);
  }

  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/events/${id}`);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>('/api/categories');
  }
}