import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { Subscription } from 'rxjs';
import { EventService } from '../../../core/services/event.service';
import { AuthService } from '../../../core/services/auth.service';
import { UserService, UserResponse } from '../../../core/services/user.service';
import { EventResponse, EventRequest, Category } from '../../../core/models/event.model';

@Component({
  selector: 'app-events-page',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './events-page.component.html',
  styleUrl: './events-page.component.css'
})
export class EventsPageComponent implements OnInit, OnDestroy {
  private eventService = inject(EventService);
  private userService = inject(UserService);
  authService = inject(AuthService);

  private eventsSub?: Subscription;

  // ── filters ──
  filterTitle = '';
  filterVenue = '';
  selectedCategoryId: number | null = null;
  categories = signal<Category[]>([]);

  // ── events list & pagination ──
  events = signal<EventResponse[]>([]);
  currentPage = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);
  pageSize = 10;
  loading = signal(true);
  errorMessage = signal('');

  // ── create / edit modal ──
  showModal = signal(false);
  isEditing = signal(false);
  editEventId: number | null = null;
  formData: EventRequest = emptyRequest();

  // ── admin ──
  users = signal<UserResponse[]>([]);
  adminLoading = signal(false);
  searchEventId = signal<number | null>(null);
  searchedEvent = signal<EventResponse | null>(null);
  searchEventLoading = signal(false);
  searchEventError = signal('');

  ngOnInit() {
    this.loadCategories();
    this.loadEvents();
    this.authService.fetchCurrentUser();
  }

  ngOnDestroy() {
    this.eventsSub?.unsubscribe();
  }

  loadCategories() {
    this.eventService.getCategories().subscribe({
      next: c => this.categories.set(c)
    });
  }

  loadEvents() {
    this.loading.set(true);
    this.errorMessage.set('');
    this.eventsSub?.unsubscribe();
    this.eventsSub = this.eventService.getEventsPage(
      this.currentPage(),
      this.pageSize,
      this.selectedCategoryId ?? undefined
    ).subscribe({
      next: page => {
        this.events.set(page.content);
        this.totalPages.set(page.totalPages);
        this.totalElements.set(page.totalElements);
        this.currentPage.set(page.number);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load events page:', err);
        this.errorMessage.set('Failed to load events.');
        this.loading.set(false);
      }
    });
  }

  search() {
    this.currentPage.set(0);
    if (!this.filterTitle && !this.filterVenue && this.selectedCategoryId === null) {
      this.loadEvents();
      return;
    }
    this.loading.set(true);
    this.eventsSub?.unsubscribe();
    this.eventsSub = this.eventService.getEvents(
      this.filterTitle || undefined,
      this.filterVenue || undefined,
      this.selectedCategoryId ?? undefined
    ).subscribe({
      next: list => {
        this.events.set(list);
        this.totalPages.set(1);
        this.totalElements.set(list.length);
        this.currentPage.set(0);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Search failed:', err);
        this.loading.set(false);
      }
    });
  }

  clearFilters() {
    this.filterTitle = '';
    this.filterVenue = '';
    this.selectedCategoryId = null;
    this.currentPage.set(0);
    this.loadEvents();
  }

  goToPage(page: number) {
    if (page < 0 || page >= this.totalPages()) return;
    this.currentPage.set(page);
    this.loadEvents();
  }

  openCreateModal() {
    this.isEditing.set(false);
    this.editEventId = null;
    this.formData = emptyRequest();
    this.showModal.set(true);
  }

  openEditModal(event: EventResponse) {
    this.isEditing.set(true);
    this.editEventId = event.id;
    this.formData = {
      title: event.title,
      description: event.description ?? '',
      venue: event.venue,
      startTime: event.startTime.substring(0, 16),
      endTime: event.endTime.substring(0, 16),
      capacity: event.capacity,
      availableTickets: event.availableTickets,
      categoryIds: []
    };
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  saveEvent() {
    const byId = (id: string) => document.getElementById(id) as HTMLInputElement;

    const title = byId('titleInput')?.value || '';
    const venue = byId('venueInput')?.value || '';
    const startDate = byId('startDateInput')?.value || '';
    const startTime = byId('startTimeInput')?.value || '09:00';
    const endDate = byId('endDateInput')?.value || '';
    const endTime = byId('endTimeInput')?.value || '17:00';
    const capacity = parseInt(byId('capacityInput')?.value || '100', 10);
    const availableTickets = parseInt(byId('ticketsInput')?.value || '100', 10);
    const description = (document.getElementById('descInput') as HTMLTextAreaElement)?.value || '';

    const catSelect = document.getElementById('categoryInput') as HTMLSelectElement;
    const categoryIds = catSelect ? Array.from(catSelect.selectedOptions).map(o => Number(o.value)) : [];

    // combine date + time into LocalDateTime format
    const startDateTime = startDate ? startDate + 'T' + startTime + ':00' : '';
    const endDateTime = endDate ? endDate + 'T' + endTime + ':00' : '';

    console.log('DOM values:', { title, venue, startDateTime, endDateTime, capacity, availableTickets, description, categoryIds });

    if (!title || !venue || !startDate || !endDate) {
      alert('Please fill all required fields (title, venue, start date, end date)');
      return;
    }

    const body: EventRequest = {
      title,
      venue,
      startTime: startDateTime,
      endTime: endDateTime,
      capacity,
      availableTickets,
      description,
      categoryIds
    };

    console.log('Saving event:', JSON.stringify(body));

    const onComplete = () => {
      this.closeModal();
      this.loadEvents();
    };
    const onError = (err: any) => {
      console.error('Save failed', err);
      alert('Failed to save event: ' + (err.error?.message || err.message || 'Unknown error'));
    };

    if (this.isEditing() && this.editEventId) {
      this.eventService.updateEvent(this.editEventId, body).subscribe({
        next: onComplete,
        error: onError
      });
    } else {
      this.eventService.createEvent(body).subscribe({
        next: onComplete,
        error: onError
      });
    }
  }

  deleteEvent(id: number) {
    if (!confirm('Delete this event?')) return;
    this.eventService.deleteEvent(id).subscribe({
      next: () => this.loadEvents()
    });
  }

  canEdit(event: EventResponse): boolean {
    const user = this.authService.currentUser();
    return user != null && (user.role === 'ADMIN' || user.id === event.organizerId);
  }

  searchEventById() {
    const id = this.searchEventId();
    if (!id) return;

    this.searchEventLoading.set(true);
    this.searchEventError.set('');
    this.searchedEvent.set(null);

    this.eventService.getEventById(id).subscribe({
      next: event => {
        this.searchedEvent.set(event);
        this.searchEventLoading.set(false);
      },
      error: () => {
        this.searchEventError.set('Event not found with id: ' + id);
        this.searchEventLoading.set(false);
      }
    });
  }

  clearSearchedEvent() {
    this.searchedEvent.set(null);
    this.searchEventError.set('');
    this.searchEventId.set(null);
  }

  loadUsers() {
    this.adminLoading.set(true);
    this.userService.getAllUsers().subscribe({
      next: list => {
        this.users.set(list);
        this.adminLoading.set(false);
      },
      error: () => this.adminLoading.set(false)
    });
  }

  deleteUser(id: number) {
    if (!confirm('Delete this user?')) return;
    this.userService.deleteUser(id).subscribe({
      next: () => this.loadUsers()
    });
  }
}

function emptyRequest(): EventRequest {
  return {
    title: '',
    description: '',
    venue: '',
    startTime: '',
    endTime: '',
    capacity: 100,
    availableTickets: 100,
    categoryIds: []
  };
}