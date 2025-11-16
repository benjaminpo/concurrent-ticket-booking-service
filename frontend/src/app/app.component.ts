import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { EventListComponent } from './components/event-list/event-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatIconModule,
    EventListComponent
  ],
  template: `
    <mat-toolbar color="primary">
      <mat-icon>confirmation_number</mat-icon>
      <span class="toolbar-title">Ticket Booking Service</span>
    </mat-toolbar>
    
    <div class="page-container">
      <app-event-list></app-event-list>
    </div>
  `,
  styles: [`
    .toolbar-title {
      margin-left: 12px;
      font-size: 20px;
      font-weight: 500;
    }

    .page-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 24px;
    }
  `]
})
export class AppComponent {
  title = 'Ticket Booking Service';
}

