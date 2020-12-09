package me.hycho.demorestapi.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import me.hycho.demorestapi.common.BaseTest;

public class indexControllerTest extends BaseTest {

    @Test
    public void index() throws Exception {
        this.mockMvc.perform(get("/api"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("_links.events").exists());
    }

}
