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

    @Test
    public void testFree() {
        Event event = Event.builder().basePrice(0).maxPrice(0).build();

        event.update();
        
        assertThat(event.isFree()).isTrue();
        /* ----- */
        event = Event.builder().basePrice(100).maxPrice(0).build();
        
        event.update();

        assertThat(event.isFree()).isFalse();
        /* ----- */
        event = Event.builder().basePrice(0).maxPrice(100).build();

        event.update();

        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffline() {
        Event event = Event.builder().location("애플스토어 가로수길점").build();

        event.update();

        assertThat(event.isOffline()).isTrue();
        /* ----- */
        event = Event.builder().build();

        event.update();

        assertThat(event.isOffline()).isFalse();
    }
}
