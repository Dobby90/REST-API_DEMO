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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import me.hycho.demorestapi.common.RestdocsConfiguration;
import me.hycho.demorestapi.common.TestDescroption;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestdocsConfiguration.class)
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
    @TestDescroption("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트") // junit4
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
    @TestDescroption("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(eventDto))
                ).andExpect(status().isBadRequest());
    }

    @Test
    @TestDescroption("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
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
                .andExpect(jsonPath("$[0].objectName").exists())
                // .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                // .andExpect(jsonPath("$[0].rejectedValue").exists())
                ;
    }

}
