export interface Event {
  id: number;
  name: string;
  description: string;
  totalTickets: number;
  availableTickets: number;
}

export interface BookingResponse {
  bookingId: number;
  eventId: number;
  eventName: string;
  ticketsBooked: number;
  remainingTickets: number;
  message: string;
}

export interface ErrorResponse {
  timestamp: string;
  message: string;
  details: string;
}

