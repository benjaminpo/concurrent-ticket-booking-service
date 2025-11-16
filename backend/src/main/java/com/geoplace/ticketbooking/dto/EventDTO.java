package com.geoplace.ticketbooking.dto;

import com.geoplace.ticketbooking.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    
    private Long id;
    private String name;
    private String description;
    private Integer totalTickets;
    private Integer availableTickets;
    
    public static EventDTO fromEntity(Event event) {
        return new EventDTO(
            event.getId(),
            event.getName(),
            event.getDescription(),
            event.getTotalTickets(),
            event.getAvailableTickets()
        );
    }
}

