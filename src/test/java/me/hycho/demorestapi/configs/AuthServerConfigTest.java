package me.hycho.demorestapi.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.hycho.demorestapi.accounts.AccountService;
import me.hycho.demorestapi.common.BaseTest;

public class AuthServerConfigTest extends BaseTest {

    @Autowired
    AccountService accountService;

    @Test
    @DisplayName("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
        String username = "hycho@redsoft.co.kr";
        String password = "hycho";
        String clientId = "myApp";
        String clientPw = "pass";

        this.mockMvc.perform(post("/oauth/token")
                        .with(httpBasic(clientId, clientPw))
                        .param("username", username)
                        .param("password", password)
                        .param("grant_type", "password")
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("access_token").exists())
                    ;
        }

}