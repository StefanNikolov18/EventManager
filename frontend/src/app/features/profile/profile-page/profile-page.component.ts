import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { EventService } from '../../../core/services/event.service';
import { EventResponse } from '../../../core/models/event.model';

@Component({
  selector: 'app-profile-page',
  imports: [RouterLink, DatePipe],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.css'
})
export class ProfilePageComponent implements OnInit {
  private authService = inject(AuthService);
  private eventService = inject(EventService);

  currentUser = this.authService.currentUser;
  myEvents: EventResponse[] = [];
  loading = true;
  error = '';

  ngOnInit() {
    this.loadMyEvents();
  }

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
}