package com.geoplace.ticketbooking.controller;

import com.geoplace.ticketbooking.dto.BookingResponse;
import com.geoplace.ticketbooking.dto.EventDTO;
import com.geoplace.ticketbooking.service.TicketBookingService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:4200")
public class TicketBookingController {
    
    private final TicketBookingService ticketBookingService;
    
    /**
     * Book tickets for an event
     * POST /api/tickets/{id}/book?count=2
     */
    @PostMapping("/tickets/{id}/book")
    public ResponseEntity<BookingResponse> bookTickets(
            @PathVariable("id") Long eventId,
            @RequestParam("count") @Min(value = 1, message = "Count must be at least 1") Integer count,
            @RequestParam(value = "userId", defaultValue = "anonymous") String userId) {
        
        BookingResponse response = ticketBookingService.bookTickets(eventId, count, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Get event details including remaining tickets
     * GET /api/tickets/{id}
     */
    @GetMapping("/tickets/{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable("id") Long eventId) {
        EventDTO event = ticketBookingService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }
    
    /**
     * Get all events
     * GET /api/events
     */
    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = ticketBookingService.getAllEvents();
        return ResponseEntity.ok(events);
    }
}

