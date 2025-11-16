package com.geoplace.ticketbooking.service;

import com.geoplace.ticketbooking.dto.BookingResponse;
import com.geoplace.ticketbooking.dto.EventDTO;
import com.geoplace.ticketbooking.entity.Booking;
import com.geoplace.ticketbooking.entity.Event;
import com.geoplace.ticketbooking.exception.EventNotFoundException;
import com.geoplace.ticketbooking.exception.InsufficientTicketsException;
import com.geoplace.ticketbooking.repository.BookingRepository;
import com.geoplace.ticketbooking.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketBookingService {
    
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    
    /**
     * Book tickets for an event with pessimistic locking to prevent overbooking
     * @param eventId The event ID
     * @param count Number of tickets to book
     * @param userId User making the booking
     * @return BookingResponse with booking details
     */
    @Transactional
    public BookingResponse bookTickets(Long eventId, Integer count, String userId) {
        log.info("Attempting to book {} tickets for event {} by user {}", count, eventId, userId);
        
        // Use pessimistic write lock to prevent concurrent modifications
        Event event = eventRepository.findByIdWithLock(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));
        
        // Check if enough tickets are available
        if (!event.canBook(count)) {
            log.warn("Insufficient tickets for event {}. Requested: {}, Available: {}", 
                     eventId, count, event.getAvailableTickets());
            throw new InsufficientTicketsException(
                String.format("Not enough tickets available. Requested: %d, Available: %d", 
                            count, event.getAvailableTickets())
            );
        }
        
        // Book the tickets
        event.bookTickets(count);
        eventRepository.save(event);
        
        // Create booking record
        Booking booking = new Booking(event, count, userId);
        booking = bookingRepository.save(booking);
        
        log.info("Successfully booked {} tickets for event {}. Remaining: {}", 
                 count, eventId, event.getAvailableTickets());
        
        return BookingResponse.success(
            booking.getId(),
            event.getId(),
            event.getName(),
            count,
            event.getAvailableTickets()
        );
    }
    
    /**
     * Get event details by ID
     * @param eventId The event ID
     * @return EventDTO with event details
     */
    public EventDTO getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));
        return EventDTO.fromEntity(event);
    }
    
    /**
     * Get all events
     * @return List of EventDTO
     */
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
            .map(EventDTO::fromEntity)
            .collect(Collectors.toList());
    }
}

