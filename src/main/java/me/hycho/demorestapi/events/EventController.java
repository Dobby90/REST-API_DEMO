package me.hycho.demorestapi.events;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.*;

import java.net.URI;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    
    @PostMapping
    public ResponseEntity createEvent(@RequestBody Event event){
        Event result = eventRepository.save(event);
        URI createUri = linkTo(EventController.class).slash(result.getId()).toUri();
        return ResponseEntity.created(createUri).body(event);
    }
}
