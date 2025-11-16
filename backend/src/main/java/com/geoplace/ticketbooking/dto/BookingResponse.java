package com.geoplace.ticketbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    
    private Long bookingId;
    private Long eventId;
    private String eventName;
    private Integer ticketsBooked;
    private Integer remainingTickets;
    private String message;
    
    public static BookingResponse success(Long bookingId, Long eventId, String eventName, 
                                         Integer ticketsBooked, Integer remainingTickets) {
        return new BookingResponse(
            bookingId,
            eventId,
            eventName,
            ticketsBooked,
            remainingTickets,
            "Booking successful"
        );
    }
}

