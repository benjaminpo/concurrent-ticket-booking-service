import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { TicketBookingService } from '../../services/ticket-booking.service';
import { Event, ErrorResponse } from '../../models/event.model';
import { HttpErrorResponse } from '@angular/common/http';

interface DialogData {
  event: Event;
}

@Component({
  selector: 'app-book-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './book-dialog.component.html',
  styleUrls: ['./book-dialog.component.scss']
})
export class BookDialogComponent {
  bookingForm: FormGroup;
  loading = false;
  event: Event;

  constructor(
    private fb: FormBuilder,
    private ticketBookingService: TicketBookingService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<BookDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {
    this.event = data.event;
    this.bookingForm = this.fb.group({
      ticketCount: [1, [
        Validators.required,
        Validators.min(1),
        Validators.max(this.event.availableTickets)
      ]],
      userId: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.bookingForm.invalid) {
      this.snackBar.open('Please fill in all fields correctly', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    this.loading = true;
    const ticketCount = this.bookingForm.value.ticketCount;
    const userId = this.bookingForm.value.userId;

    this.ticketBookingService.bookTickets(this.event.id, ticketCount, userId).subscribe({
      next: (response) => {
        this.loading = false;
        this.snackBar.open(
          `Successfully booked ${response.ticketsBooked} ticket(s) for ${response.eventName}! ${response.remainingTickets} tickets remaining.`,
          'Close',
          {
            duration: 5000,
            panelClass: ['success-snackbar']
          }
        );
        this.dialogRef.close(true);
      },
      error: (error: HttpErrorResponse) => {
        this.loading = false;
        let errorMessage = 'Failed to book tickets';
        
        if (error.error && typeof error.error === 'object') {
          const errorResponse = error.error as ErrorResponse;
          errorMessage = errorResponse.details || errorResponse.message || errorMessage;
        } else if (error.message) {
          errorMessage = error.message;
        }
        
        this.snackBar.open(errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }

  get ticketCount() {
    return this.bookingForm.get('ticketCount');
  }

  get userId() {
    return this.bookingForm.get('userId');
  }
}

