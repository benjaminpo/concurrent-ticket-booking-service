import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TicketBookingService } from '../../services/ticket-booking.service';
import { Event } from '../../models/event.model';
import { BookDialogComponent } from '../book-dialog/book-dialog.component';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialogModule
  ],
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.scss']
})
export class EventListComponent implements OnInit {
  events: Event[] = [];
  displayedColumns: string[] = ['name', 'description', 'totalTickets', 'availableTickets', 'action'];
  loading = true;

  constructor(
    private ticketBookingService: TicketBookingService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.loading = true;
    this.ticketBookingService.getAllEvents().subscribe({
      next: (events) => {
        this.events = events;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading events:', error);
        this.snackBar.open('Failed to load events', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
        this.loading = false;
      }
    });
  }

  openBookingDialog(event: Event): void {
    if (event.availableTickets === 0) {
      this.snackBar.open('No tickets available for this event', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    const dialogRef = this.dialog.open(BookDialogComponent, {
      width: '400px',
      data: { event }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Refresh the event list after successful booking
        this.loadEvents();
      }
    });
  }

  getAvailabilityClass(availableTickets: number): string {
    if (availableTickets === 0) {
      return 'sold-out';
    } else if (availableTickets < 20) {
      return 'low-availability';
    }
    return 'available';
  }

  isBookingDisabled(event: Event): boolean {
    return event.availableTickets === 0;
  }
}

