package me.hycho.demorestapi.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import me.hycho.demorestapi.common.RestdocsConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestdocsConfiguration.class)
@ActiveProfiles("test")
public class indexControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void index() throws Exception {
        this.mockMvc.perform(get("/api"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("_links.events").exists());
    }

}
