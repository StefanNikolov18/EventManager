import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { SessionService } from '../../../core/services/session.service';
import { SpeakerService } from '../../../core/services/speaker.service';
import { PresentationMaterialService } from '../../../core/services/presentation-material.service';
import { EventService } from '../../../core/services/event.service';
import { AuthService } from '../../../core/services/auth.service';
import {
  SessionResponse, SessionRequest, SESSION_TYPES, SessionType,
  SpeakerResponse, SpeakerRequest
} from '../../../core/models/session.model';
import { EventResponse } from '../../../core/models/event.model';

interface SpeakerForm {
  id?: number;
  name: string;
  biography: string;
  companyName: string;
  photoUrl: string;
  websiteUrl: string;
}

function emptySpeaker(): SpeakerForm {
  return { name: '', biography: '', companyName: '', photoUrl: '', websiteUrl: '' };
}

@Component({
  selector: 'app-sessions-page',
  standalone: true,
  imports: [FormsModule, DatePipe, RouterLink],
  templateUrl: './sessions-page.component.html',
  styleUrl: './sessions-page.component.css'
})
export class SessionsPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private sessionService = inject(SessionService);
  private speakerService = inject(SpeakerService);
  private materialService = inject(PresentationMaterialService);
  private eventService = inject(EventService);
  authService = inject(AuthService);

  eventId = signal<number>(0);
  event = signal<EventResponse | null>(null);
  sessions = signal<SessionResponse[]>([]);
  loading = signal(true);
  saving = signal(false);
  errorMessage = signal('');
  sessionTypes = SESSION_TYPES;

  // ── create / edit modal ──
  showModal = signal(false);
  isEditing = signal(false);
  editSessionId: number | null = null;

  // form fields
  formTitle = '';
  formDescription = '';
  formDate = '';
  formStartTime = '09:00';
  formEndTime = '17:00';
  formLocation = '';
  formOrderIndex = 1;
  formType: SessionType = 'TALK';

  // speakers
  formSpeakers: SpeakerForm[] = [];

  // presentation material (optional)
  formMaterialUrl = '';
  formMaterialType = '';

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('eventId'));
    if (!id) {
      this.errorMessage.set('Invalid event ID');
      this.loading.set(false);
      return;
    }
    this.eventId.set(id);
    this.loadEvent();
    this.loadSessions();
  }

  loadEvent() {
    this.eventService.getEventById(this.eventId()).subscribe({
      next: ev => this.event.set(ev),
      error: () => this.errorMessage.set('Failed to load event details.')
    });
  }

  loadSessions() {
    this.sessionService.getSessionsByEvent(this.eventId()).subscribe({
      next: list => {
        this.sessions.set(list);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load sessions.');
        this.loading.set(false);
      }
    });
  }

  // ── speaker form helpers ──

  addSpeaker() {
    this.formSpeakers.push(emptySpeaker());
  }

  removeSpeaker(index: number) {
    this.formSpeakers.splice(index, 1);
  }

  // ── modal ──

  openCreateModal() {
    this.isEditing.set(false);
    this.editSessionId = null;
    this.resetForm();
    this.showModal.set(true);
  }

  openEditModal(session: SessionResponse) {
    this.isEditing.set(true);
    this.editSessionId = session.id;
    this.formTitle = session.title;
    this.formDescription = session.description || '';
    const st = session.startTime.substring(0, 16);
    const et = session.endTime.substring(0, 16);
    this.formDate = st.substring(0, 10);
    this.formStartTime = st.substring(11, 16);
    this.formEndTime = et.substring(11, 16);
    this.formLocation = session.locationRoom || '';
    this.formOrderIndex = session.orderIndex;
    this.formType = session.type;
    // populate speakers from existing session
    this.formSpeakers = (session.speakers || []).map(s => ({
      id: s.id,
      name: s.name,
      biography: s.biography || '',
      companyName: s.companyName || '',
      photoUrl: s.photoUrl || '',
      websiteUrl: s.websiteUrl || ''
    }));
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  resetForm() {
    this.formTitle = '';
    this.formDescription = '';
    this.formDate = '';
    this.formStartTime = '09:00';
    this.formEndTime = '17:00';
    this.formLocation = '';
    this.formOrderIndex = 1;
    this.formType = 'TALK';
    this.formSpeakers = [];
    this.formMaterialUrl = '';
    this.formMaterialType = '';
  }

  saveSession() {
    if (!this.formTitle || !this.formDate) {
      alert('Please fill in the title and date.');
      return;
    }

    const hasMaterial = this.formMaterialUrl.trim().length > 0;
    const validSpeakers = this.formSpeakers.filter(s => s.name.trim());

    if (hasMaterial && validSpeakers.length === 0) {
      alert('Please add at least one speaker if you want to attach a presentation material.');
      return;
    }

    this.saving.set(true);

    const startDateTime = this.formDate + 'T' + this.formStartTime + ':00';
    const endDateTime = this.formDate + 'T' + this.formEndTime + ':00';

    let body: SessionRequest = {
      title: this.formTitle,
      description: this.formDescription,
      startTime: startDateTime,
      endTime: endDateTime,
      orderIndex: this.formOrderIndex,
      locationRoom: this.formLocation,
      type: this.formType,
      speakerIds: []
    };

    const onError = (err: any) => {
      console.error('Session save failed', err);
      this.saving.set(false);
      alert('Failed to save session: ' + (err.error?.message || err.message || 'Unknown error'));
    };

    if (this.isEditing() && this.editSessionId) {
      // ── Update session + reconcile speakers ──

      if (validSpeakers.length === 0) {
        // no speakers — just update with empty array
        this.sessionService.updateSession(this.editSessionId!, body).subscribe({
          next: () => {
            this.closeModal();
            this.saving.set(false);
            this.loadSessions();
          },
          error: onError
        });
        return;
      }

      const speakerIds: number[] = [];
      let completed = 0;
      const total = validSpeakers.length;

      const finishUpdate = () => {
        body = { ...body, speakerIds };
        this.sessionService.updateSession(this.editSessionId!, body).subscribe({
          next: () => {
            this.closeModal();
            this.saving.set(false);
            this.loadSessions();
          },
          error: onError
        });
      };

      validSpeakers.forEach(speaker => {
        const req: SpeakerRequest = {
          name: speaker.name,
          biography: speaker.biography,
          companyName: speaker.companyName,
          photoUrl: speaker.photoUrl,
          websiteUrl: speaker.websiteUrl
        };

        if (speaker.id) {
          // existing speaker — update in place
          this.speakerService.updateSpeaker(speaker.id, req).subscribe({
            next: () => {
              speakerIds.push(speaker.id!);
              completed++;
              if (completed === total) finishUpdate();
            },
            error: (err) => {
              console.error('Failed to update speaker', err);
              completed++;
              if (completed === total) finishUpdate();
            }
          });
        } else {
          // new speaker — create and associate with this session
          this.speakerService.createSpeaker(this.editSessionId!, req).subscribe({
            next: (created) => {
              speakerIds.push(created.id);
              completed++;
              if (completed === total) finishUpdate();
            },
            error: (err) => {
              console.error('Failed to create speaker', err);
              completed++;
              if (completed === total) finishUpdate();
            }
          });
        }
      });
    } else {
      this.sessionService.createSession(this.eventId(), body).subscribe({
        next: savedSession => {
          if (validSpeakers.length === 0) {
            this.closeModal();
            this.saving.set(false);
            this.loadSessions();
            return;
          }

          const speakerIds: number[] = [];
          let completed = 0;
          const total = validSpeakers.length;

          const finish = () => {
            if (hasMaterial && speakerIds.length > 0) {
              this.materialService.createMaterial(speakerIds[0], {
                speakerId: speakerIds[0],
                sessionId: savedSession.id,
                fileUrl: this.formMaterialUrl,
                fileType: this.formMaterialType
              }).subscribe({
                next: () => {
                  this.closeModal();
                  this.saving.set(false);
                  this.loadSessions();
                },
                error: (err) => {
                  console.error('Failed to save material', err);
                  alert('Session created, but failed to save presentation material: ' + (err.error?.message || err.message || 'Unknown error'));
                  this.closeModal();
                  this.saving.set(false);
                  this.loadSessions();
                }
              });
            } else {
              this.closeModal();
              this.saving.set(false);
              this.loadSessions();
            }
          };

          validSpeakers.forEach(speaker => {
            const req: SpeakerRequest = {
              name: speaker.name,
              biography: speaker.biography,
              companyName: speaker.companyName,
              photoUrl: speaker.photoUrl,
              websiteUrl: speaker.websiteUrl
            };
            this.speakerService.createSpeaker(savedSession.id, req).subscribe({
              next: (createdSpeaker) => {
                speakerIds.push(createdSpeaker.id);
                completed++;
                if (completed === total) finish();
              },
              error: (err) => {
                console.error('Failed to create speaker', err);
                completed++;
                if (completed === total) finish();
              }
            });
          });
        },
        error: onError
      });
    }
  }

  deleteSession(id: number) {
    if (!confirm('Delete this session?')) return;
    this.sessionService.deleteSession(id).subscribe({
      next: () => this.loadSessions()
    });
  }

  canManage(event: EventResponse | null): boolean {
    if (!event) return false;
    const user = this.authService.currentUser();
    return user != null && (user.role === 'ADMIN' || user.id === event.organizerId);
  }

  getSpeakerNames(speakers: SpeakerResponse[]): string {
    return speakers.map(s => s.name).join(', ');
  }

  trackByIndex(index: number): number {
    return index;
  }
}