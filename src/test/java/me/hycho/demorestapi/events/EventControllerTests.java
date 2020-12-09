package me.hycho.demorestapi.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import me.hycho.demorestapi.common.BaseTest;

public class EventControllerTests extends BaseTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트") // junit5
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder().name("spring").description("rest api with spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 23, 14, 40))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 24, 14, 40))
                .beginEventDateTime(LocalDateTime.of(2020, 11, 25, 14, 40))
                .endEventDateTime(LocalDateTime.of(2020, 11, 26, 14, 40)).basePrice(100).maxPrice(200)
                .limitOfEnrollment(100).location("애플스토어 가로수길점").build();

        mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))).andDo(print()).andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists()).andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("free").value(false)).andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event", links(
                                                    linkWithRel("self").description("link to self"),
                                                    linkWithRel("query-events").description("link to query events"),
                                                    linkWithRel("update-event").description("link to update an existing event"),
                                                    linkWithRel("profile").description("link to profile")
                                                ),
                                                requestHeaders(
                                                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content-type header")
                                                ),
                                                requestFields(
                                                    fieldWithPath("name").description("name of new event"),
                                                    fieldWithPath("description").description("description of new event"),
                                                    fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                                    fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                                    fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                                    fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                                    fieldWithPath("location").description("location of new event"),
                                                    fieldWithPath("basePrice").description("basePrice of new event"),
                                                    fieldWithPath("maxPrice").description("maxPrice of new event"),
                                                    fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
                                                ),
                                                responseHeaders(
                                                    headerWithName(HttpHeaders.LOCATION).description("location header"),
                                                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content-type header")
                                                ),
                                                responseFields( // relaxedResponseFields : 문서의 일부분만 확인해도 되게끔 설정해주는 prefix
                                                    fieldWithPath("id").description("id of new event"),
                                                    fieldWithPath("name").description("name of new event"),
                                                    fieldWithPath("description").description("description of new event"),
                                                    fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                                    fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                                    fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                                    fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                                    fieldWithPath("location").description("location of new event"),
                                                    fieldWithPath("basePrice").description("basePrice of new event"),
                                                    fieldWithPath("maxPrice").description("maxPrice of new event"),
                                                    fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                                                    fieldWithPath("free").description("it tells if this event is free or not"),
                                                    fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                                    fieldWithPath("eventStatus").description("event status"),
                                                    fieldWithPath("_links.self.href").description("link to self"),
                                                    fieldWithPath("_links.query-events.href").description("link to query event list"),
                                                    fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                                    fieldWithPath("_links.profile.href").description("link to profile")
                                                )                                                                        

                                )
                        )
                ;
    }

    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("spring")
                .description("rest api with spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 23, 14, 40))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 24, 14, 40))
                .beginEventDateTime(LocalDateTime.of(2020, 11, 25, 14, 40))
                .endEventDateTime(LocalDateTime.of(2020, 11, 26, 14, 40))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("애플스토어 가로수길점")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON)
                                            .accept(MediaTypes.HAL_JSON)
                                            .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(eventDto))
                ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("spring").description("rest api with spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 24, 14, 40))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 23, 14, 40))
                .beginEventDateTime(LocalDateTime.of(2020, 11, 26, 14, 40))
                .endEventDateTime(LocalDateTime.of(2020, 11, 25, 14, 40))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("애플스토어 여의도점")
                .build();

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
                ;
    }

    @Test
    @DisplayName("Total=30, pageSize=10, pageNo=2 조회")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When & Then
        this.mockMvc.perform(get("/api/events")
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
                ;
        
    }

    @Test
    @DisplayName("기존의 이벤트를 1건 조회")
    public void getEvent() throws Exception {
        //Given
        Event event = this.generateEvent(100);

        // When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-event"));
    }

    @Test
    @DisplayName("없는 이벤트를 조회 했을 경우: 404 응답")
    public void getEvent404() throws Exception {
        // When & Then
        this.mockMvc.perform(get("/api/events/1205"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이벤트를 정상적으로 수정")
    public void updateEvent() throws Exception {
        // Given
        Event event = this.generateEvent(100);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Update Event";
        eventDto.setName(eventName);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(eventDto))
                            )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event"));
    }

    @Test
    @DisplayName("입력 값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEvent400_Empty() throws Exception {
        // Given
        Event event = this.generateEvent(100);
        EventDto eventDto = new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(eventDto))
                            )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400_Wrong() throws Exception {
        // Given
        Event event = this.generateEvent(100);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(10000);
        eventDto.setMaxPrice(5000);

        // When & Then
        this.mockMvc
                .perform(put("/api/events/{id}", event.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.objectMapper.writeValueAsString(eventDto))
                        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404() throws Exception {
        // Given
        Event event = this.generateEvent(100);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // When & Then
        this.mockMvc
                .perform(put("/api/events/1205")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(this.objectMapper.writeValueAsString(eventDto))
                        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Event generateEvent(int index) {
        Event event = buildEvent(index);
        return this.eventRepository.save(event);
    }

    private Event buildEvent(int index) {
        return Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2020, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2020, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("애플스토어 가로수길점")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
    }

}
