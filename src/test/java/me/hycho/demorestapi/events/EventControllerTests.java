package me.hycho.demorestapi.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import me.hycho.demorestapi.common.TestDescroption;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {
    
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // @MockBean
    // EventRepository eventRepository;

    @Autowired
    private WebApplicationContext ctx;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
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
                            .build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                // .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                ;
    }

    @Test
    @TestDescroption("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
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
