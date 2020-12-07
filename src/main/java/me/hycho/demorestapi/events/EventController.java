package me.hycho.demorestapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.*;

import java.net.URI;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    /**
     * 이벤트 생성
     * @param event
     * @return
     */
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        };

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        };

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event result = eventRepository.save(event);
        URI createUri = linkTo(EventController.class).slash(result.getId()).toUri();

        return ResponseEntity.created(createUri).body(event);
    }
}
