export interface EventResponse {
  id: number;
  organizerId: number;
  title: string;
  description: string;
  venue: string;
  startTime: string;
  endTime: string;
  capacity: number;
  availableTickets: number;
  categories: string[];
  ticketPrice: number;
  currency: string | null;
}

export interface RegistrationResponse {
  id: number;
  eventId: number;
  userId: number;
  status: string;
  registrationDate: string;
  entryCode: string;
}

export interface TicketResponse {
  id: number;
  registrationId: number;
  price: number;
  currency: string;
}

export interface EventRequest {
  title: string;
  description: string;
  venue: string;
  startTime: string;
  endTime: string;
  capacity: number;
  availableTickets: number;
  categoryIds: number[];
  ticketPrice: number;
  currency: string | null;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface Category {
  id: number;
  categoryName: string;
}