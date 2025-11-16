package com.geoplace.ticketbooking.config;

import com.geoplace.ticketbooking.entity.Event;
import com.geoplace.ticketbooking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final EventRepository eventRepository;
    
    @Override
    public void run(String... args) {
        log.info("Initializing sample events...");
        
        Event event1 = new Event("Spring Boot Conference 2025", 
                                 "Annual Spring Boot developer conference", 100);
        Event event2 = new Event("Angular Workshop", 
                                 "Full-day Angular hands-on workshop", 50);
        Event event3 = new Event("Java 21 Masterclass", 
                                 "Deep dive into Java 21 features", 75);
        Event event4 = new Event("Microservices Summit", 
                                 "Learn about microservices architecture", 120);
        Event event5 = new Event("Cloud Native Conference", 
                                 "Cloud-native technologies and best practices", 200);
        
        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);
        eventRepository.save(event4);
        eventRepository.save(event5);
        
        log.info("Sample events initialized successfully");
    }
}

