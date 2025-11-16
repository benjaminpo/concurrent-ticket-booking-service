package com.geoplace.ticketbooking.service;

import com.geoplace.ticketbooking.dto.BookingResponse;
import com.geoplace.ticketbooking.dto.EventDTO;
import com.geoplace.ticketbooking.entity.Event;
import com.geoplace.ticketbooking.exception.EventNotFoundException;
import com.geoplace.ticketbooking.exception.InsufficientTicketsException;
import com.geoplace.ticketbooking.repository.BookingRepository;
import com.geoplace.ticketbooking.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TicketBookingServiceTest {
    
    @Autowired
    private TicketBookingService ticketBookingService;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    private Event testEvent;
    
    @BeforeEach
    void setUp() {
        // Clear existing data
        bookingRepository.deleteAll();
        eventRepository.deleteAll();
        
        // Create a test event with 100 tickets
        testEvent = new Event("Test Event", "Test Description", 100);
        testEvent = eventRepository.save(testEvent);
    }
    
    @Test
    void testBookTickets_Success() {
        // Arrange
        Long eventId = testEvent.getId();
        Integer count = 5;
        String userId = "user1";
        
        // Act
        BookingResponse response = ticketBookingService.bookTickets(eventId, count, userId);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getBookingId());
        assertEquals(eventId, response.getEventId());
        assertEquals(count, response.getTicketsBooked());
        assertEquals(95, response.getRemainingTickets());
        
        // Verify event state
        EventDTO event = ticketBookingService.getEvent(eventId);
        assertEquals(95, event.getAvailableTickets());
    }
    
    @Test
    void testBookTickets_InsufficientTickets() {
        // Arrange
        Long eventId = testEvent.getId();
        Integer count = 101; // More than available
        String userId = "user1";
        
        // Act & Assert
        assertThrows(InsufficientTicketsException.class, () -> {
            ticketBookingService.bookTickets(eventId, count, userId);
        });
        
        // Verify event state unchanged
        EventDTO event = ticketBookingService.getEvent(eventId);
        assertEquals(100, event.getAvailableTickets());
    }
    
    @Test
    void testBookTickets_EventNotFound() {
        // Arrange
        Long nonExistentEventId = 9999L;
        Integer count = 5;
        String userId = "user1";
        
        // Act & Assert
        assertThrows(EventNotFoundException.class, () -> {
            ticketBookingService.bookTickets(nonExistentEventId, count, userId);
        });
    }
    
    @Test
    void testConcurrentBooking_NoOverbooking() throws InterruptedException, ExecutionException {
        // Arrange
        Long eventId = testEvent.getId();
        int numberOfThreads = 20;
        int ticketsPerBooking = 5;
        int expectedSuccessfulBookings = 20; // 100 tickets / 5 per booking = 20
        
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<BookingResult>> futures = new ArrayList<>();
        
        // Act - Submit 20 concurrent booking requests
        for (int i = 0; i < numberOfThreads; i++) {
            final int userId = i;
            Future<BookingResult> future = executorService.submit(() -> {
                try {
                    BookingResponse response = ticketBookingService.bookTickets(
                        eventId, ticketsPerBooking, "user" + userId
                    );
                    return new BookingResult(true, response);
                } catch (InsufficientTicketsException e) {
                    return new BookingResult(false, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }
        
        // Wait for all threads to complete
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);
        
        // Assert - Count successful bookings
        int successfulBookings = 0;
        for (Future<BookingResult> future : futures) {
            BookingResult result = future.get();
            if (result.success) {
                successfulBookings++;
            }
        }
        
        assertEquals(expectedSuccessfulBookings, successfulBookings, 
                    "Expected exactly 20 successful bookings");
        
        // Verify final event state
        EventDTO event = ticketBookingService.getEvent(eventId);
        assertEquals(0, event.getAvailableTickets(), 
                    "All tickets should be booked");
    }
    
    @Test
    void testConcurrentBooking_MultipleEventsNoConflict() throws InterruptedException, ExecutionException {
        // Arrange - Create multiple events
        Event event1 = new Event("Event 1", "Description 1", 50);
        Event event2 = new Event("Event 2", "Description 2", 50);
        event1 = eventRepository.save(event1);
        event2 = eventRepository.save(event2);
        
        Long event1Id = event1.getId();
        Long event2Id = event2.getId();
        
        int numberOfThreads = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<BookingResult>> futures = new ArrayList<>();
        
        // Act - Submit concurrent bookings for both events
        for (int i = 0; i < numberOfThreads; i++) {
            final int userId = i;
            final Long eventId = (i % 2 == 0) ? event1Id : event2Id;
            
            Future<BookingResult> future = executorService.submit(() -> {
                try {
                    BookingResponse response = ticketBookingService.bookTickets(
                        eventId, 5, "user" + userId
                    );
                    return new BookingResult(true, response);
                } catch (InsufficientTicketsException e) {
                    return new BookingResult(false, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }
        
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);
        
        // Assert
        int successfulBookings = 0;
        for (Future<BookingResult> future : futures) {
            if (future.get().success) {
                successfulBookings++;
            }
        }
        
        assertEquals(20, successfulBookings, 
                    "All bookings should succeed for separate events");
        
        // Verify both events are fully booked
        EventDTO eventDto1 = ticketBookingService.getEvent(event1Id);
        EventDTO eventDto2 = ticketBookingService.getEvent(event2Id);
        
        assertEquals(0, eventDto1.getAvailableTickets());
        assertEquals(0, eventDto2.getAvailableTickets());
    }
    
    @Test
    void testConcurrentBooking_VaryingTicketCounts() throws InterruptedException, ExecutionException {
        // Arrange
        Long eventId = testEvent.getId();
        int numberOfThreads = 25;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<BookingResult>> futures = new ArrayList<>();
        AtomicInteger totalBookedTickets = new AtomicInteger(0);
        
        // Act - Submit bookings with varying ticket counts
        for (int i = 0; i < numberOfThreads; i++) {
            final int ticketCount = (i % 5) + 1; // 1 to 5 tickets
            final int userId = i;
            
            Future<BookingResult> future = executorService.submit(() -> {
                try {
                    BookingResponse response = ticketBookingService.bookTickets(
                        eventId, ticketCount, "user" + userId
                    );
                    totalBookedTickets.addAndGet(ticketCount);
                    return new BookingResult(true, response);
                } catch (InsufficientTicketsException e) {
                    return new BookingResult(false, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }
        
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);
        
        // Wait for all to complete
        for (Future<BookingResult> future : futures) {
            future.get();
        }
        
        // Assert - Verify no overbooking occurred
        EventDTO event = ticketBookingService.getEvent(eventId);
        int remainingTickets = event.getAvailableTickets();
        
        assertTrue(remainingTickets >= 0, "Should not have negative tickets");
        assertEquals(100, totalBookedTickets.get() + remainingTickets, 
                    "Total booked + remaining should equal initial tickets");
    }
    
    @Test
    void testConcurrentBooking_HighContention() throws InterruptedException, ExecutionException {
        // Arrange - Create event with only 10 tickets
        Event limitedEvent = new Event("Limited Event", "Only 10 tickets", 10);
        limitedEvent = eventRepository.save(limitedEvent);
        Long eventId = limitedEvent.getId();
        
        int numberOfThreads = 50; // 50 threads competing for 10 tickets
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<BookingResult>> futures = new ArrayList<>();
        
        // Act - All threads try to book 1 ticket
        for (int i = 0; i < numberOfThreads; i++) {
            final int userId = i;
            Future<BookingResult> future = executorService.submit(() -> {
                try {
                    BookingResponse response = ticketBookingService.bookTickets(
                        eventId, 1, "user" + userId
                    );
                    return new BookingResult(true, response);
                } catch (InsufficientTicketsException e) {
                    return new BookingResult(false, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }
        
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);
        
        // Assert - Exactly 10 should succeed
        int successfulBookings = 0;
        int failedBookings = 0;
        
        for (Future<BookingResult> future : futures) {
            if (future.get().success) {
                successfulBookings++;
            } else {
                failedBookings++;
            }
        }
        
        assertEquals(10, successfulBookings, "Exactly 10 bookings should succeed");
        assertEquals(40, failedBookings, "40 bookings should fail");
        
        // Verify final state
        EventDTO event = ticketBookingService.getEvent(eventId);
        assertEquals(0, event.getAvailableTickets());
    }
    
    // Helper class to store booking results
    private static class BookingResult {
        boolean success;
        BookingResponse response;
        
        BookingResult(boolean success, BookingResponse response) {
            this.success = success;
            this.response = response;
        }
    }
}

