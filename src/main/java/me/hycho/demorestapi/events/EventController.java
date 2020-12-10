package me.hycho.demorestapi.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import me.hycho.demorestapi.accounts.Account;
import me.hycho.demorestapi.accounts.CurrentUser;
import me.hycho.demorestapi.common.ErrorsResource;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    /**
     * 이벤트 생성
     * @param eventDto
     * @param errors
     * @return
     */
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors, @CurrentUser Account account) {
        /**
         * Event 도메인은 java Bean스펙을 준수하고 있기 때문에 serializable할 수 있지만 에러 객체는 할 수 없다.
         */
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        ;

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        ;

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManger(account);
        Event result = eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(result.getId());
        URI createUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createUri).body(eventResource);
    }

    /**
     * 이벤트 목록 조회
     * @param pageable
     * @param assembler
     * @return
     */
    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler, @CurrentUser Account account) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        var pageResources = assembler.toModel(page, e -> new EventResource(e));    // page Resources 정보
        pageResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        if(account != null) {
            pageResources.add(linkTo(EventController.class).withRel("create-event"));
        }

        return ResponseEntity.ok(pageResources);
    }

    /**
     * 이벤트 단건 조회
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id, @CurrentUser Account account) {
        Optional<Event> findById = this.eventRepository.findById(id);
        if(findById.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = findById.get();
        event.setManger(account);
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if (event.getManger().equals(account)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }

        return ResponseEntity.ok(eventResource);
    }

    /**
     * 이벤트 수정
     * @param id
     * @param eventDto
     * @param errors
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors) {
        Optional<Event> findById = this.eventRepository.findById(id);
        if (findById.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = findById.get();

        this.modelMapper.map(eventDto, event);  // eventDto -> event Data binding
        Event savedEvent = this.eventRepository.save(event);
        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }
 
    private ResponseEntity<ErrorsResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
    
}
