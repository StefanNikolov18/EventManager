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