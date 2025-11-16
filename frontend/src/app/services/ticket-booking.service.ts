import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Event, BookingResponse } from '../models/event.model';

@Injectable({
  providedIn: 'root'
})
export class TicketBookingService {
  private readonly API_URL = '/api';

  constructor(private http: HttpClient) { }

  /**
   * Get all events
   */
  getAllEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.API_URL}/events`);
  }

  /**
   * Get event by ID
   */
  getEvent(eventId: number): Observable<Event> {
    return this.http.get<Event>(`${this.API_URL}/tickets/${eventId}`);
  }

  /**
   * Book tickets for an event
   */
  bookTickets(eventId: number, count: number, userId: string = 'anonymous'): Observable<BookingResponse> {
    const params = new HttpParams()
      .set('count', count.toString())
      .set('userId', userId);
    
    return this.http.post<BookingResponse>(`${this.API_URL}/tickets/${eventId}/book`, null, { params });
  }
}

