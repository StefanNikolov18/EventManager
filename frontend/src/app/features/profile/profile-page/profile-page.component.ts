import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { EventService } from '../../../core/services/event.service';
import { EventResponse, RegistrationResponse, TicketResponse } from '../../../core/models/event.model';

@Component({
  selector: 'app-profile-page',
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css'
})
export class ProfilePageComponent implements OnInit {
  private authService = inject(AuthService);
  private eventService = inject(EventService);

  currentUser = this.authService.currentUser;
  myEvents: EventResponse[] = [];
  myRegistrations: RegistrationResponse[] = [];
  myTickets: TicketResponse[] = [];

  // enriched data: combine registration + event + ticket
  enrichedRegistrations: EnrichedRegistration[] = [];

  loading = true;
  ticketsLoading = true;
  error = '';
  ticketsError = '';

  ngOnInit() {
    this.loadMyEvents();
    this.loadMyRegistrations();
  }

  // =============== My Events (organized) ===============

  private loadMyEvents(): void {
    const userId = this.currentUser()?.id;
    if (userId == null) {
      this.error = 'Could not identify user.';
      this.loading = false;
      return;
    }

    this.eventService.getEvents(undefined, undefined, undefined, userId).subscribe({
      next: (events) => {
        this.myEvents = events;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load your events. Please try again.';
        this.loading = false;
      }
    });
  }

  // =============== My Tickets (attending) ===============

  private loadMyRegistrations(): void {
    const userId = this.currentUser()?.id;
    if (userId == null) {
      this.ticketsError = 'Could not identify user.';
      this.ticketsLoading = false;
      return;
    }

    this.eventService.getMyRegistrations().subscribe({
      next: (registrations) => {
        this.myRegistrations = registrations;
        this.loadMyTickets();
      },
      error: () => {
        this.ticketsError = 'Failed to load your registrations.';
        this.ticketsLoading = false;
      }
    });
  }

  private loadMyTickets(): void {
    this.eventService.getMyTickets().subscribe({
      next: (tickets) => {
        this.myTickets = tickets;
        this.enrichRegistrations();
      },
      error: () => {
        this.ticketsError = 'Failed to load your tickets.';
        this.ticketsLoading = false;
      }
    });
  }

  private enrichRegistrations(): void {
    if (this.myRegistrations.length === 0) {
      this.ticketsLoading = false;
      this.enrichedRegistrations = [];
      return;
    }

    // collect unique eventIds from registrations
    const eventIds = [...new Set(this.myRegistrations.map(r => r.eventId))];

    // build ticket lookup by registrationId
    const ticketByRegId = new Map<number, TicketResponse>();
    this.myTickets.forEach(t => ticketByRegId.set(t.registrationId, t));

    // fetch event details for each registration
    const fetchPromises = eventIds.map(eventId =>
      firstValueFrom(this.eventService.getEventById(eventId))
    );

    Promise.all(fetchPromises).then((events) => {
      const eventMap = new Map<number, EventResponse>();
      events.forEach(ev => {
        if (ev) eventMap.set(ev.id, ev);
      });

      this.enrichedRegistrations = this.myRegistrations.map(reg => ({
        registration: reg,
        event: eventMap.get(reg.eventId) ?? null,
        ticket: ticketByRegId.get(reg.id) ?? null
      }));

      this.ticketsLoading = false;
    }).catch(() => {
      // fallback: show registrations without event details
      this.enrichedRegistrations = this.myRegistrations.map(reg => ({
        registration: reg,
        event: null,
        ticket: ticketByRegId.get(reg.id) ?? null
      }));
      this.ticketsLoading = false;
    });
  }
}

interface EnrichedRegistration {
  registration: RegistrationResponse;
  event: EventResponse | null;
  ticket: TicketResponse | null;
}