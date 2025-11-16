package com.geoplace.ticketbooking.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    
    @Min(value = 1, message = "Count must be at least 1")
    private Integer count;
    
    private String userId;
}

