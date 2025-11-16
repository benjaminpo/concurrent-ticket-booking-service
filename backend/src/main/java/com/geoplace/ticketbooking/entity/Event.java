package com.geoplace.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private Integer totalTickets;
    
    @Column(nullable = false)
    private Integer availableTickets;
    
    @Version
    private Long version;
    
    public Event(String name, String description, Integer totalTickets) {
        this.name = name;
        this.description = description;
        this.totalTickets = totalTickets;
        this.availableTickets = totalTickets;
    }
    
    public boolean canBook(int count) {
        return availableTickets >= count && count > 0;
    }
    
    public void bookTickets(int count) {
        if (!canBook(count)) {
            throw new IllegalStateException("Not enough tickets available");
        }
        this.availableTickets -= count;
    }
}

