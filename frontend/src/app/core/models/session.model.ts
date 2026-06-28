export interface SessionResponse {
  id: number;
  eventId: number;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  orderIndex: number;
  locationRoom: string;
  type: SessionType;
  speakers: SpeakerResponse[];
}

export interface SessionRequest {
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  orderIndex: number;
  locationRoom: string;
  type: SessionType;
  speakerIds: number[];
}

export interface SpeakerResponse {
  id: number;
  creatorId: number;
  name: string;
  biography: string;
  companyName: string;
  photoUrl: string;
  websiteUrl: string;
}

export interface SpeakerRequest {
  name: string;
  biography: string;
  companyName: string;
  photoUrl: string;
  websiteUrl: string;
}

export interface PresentationMaterialResponse {
  id: number;
  speakerId: number;
  sessionId: number;
  fileUrl: string;
  fileType: string;
  uploadTime: string;
}

export interface PresentationMaterialRequest {
  speakerId: number;
  fileUrl: string;
  fileType: string;
}

export type SessionType = 'KEYNOTE' | 'TALK' | 'WORKSHOP' | 'PANEL' | 'BREAK';

export const SESSION_TYPES: SessionType[] = [
  'KEYNOTE',
  'TALK',
  'WORKSHOP',
  'PANEL',
  'BREAK'
];