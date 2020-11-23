package me.hycho.demorestapi.events;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class EventTest {
    
    @Test
    public void builder() {
        Event event = Event.builder()
                .name("DEMO REST API")
                .description("REST API WITH SPRING BOOT")
                .build();
        
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        
        String name = "Event";
        String description = "Spring";
        
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}
