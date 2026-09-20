package com.fitness.platform.user.controller;

import com.fitness.platform.user.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    @Test
    @WithMockUser
    void testGetProfile_ForbiddenWithoutJwt() throws Exception {
        // Without JWT auth it might be 401/403, we just check context loads or simple auth
        mockMvc.perform(get("/api/users/me"))
               .andExpect(status().isUnauthorized()); // Or however security is mocked
    }
}
