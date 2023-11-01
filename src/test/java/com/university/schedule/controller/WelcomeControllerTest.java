package com.university.schedule.controller;

import com.university.schedule.config.WebTestConfig;
import com.university.schedule.model.Role;
import com.university.schedule.model.User;
import com.university.schedule.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = WelcomeController.class)
@Import(WebTestConfig.class)
@ActiveProfiles(value = "test")
public class WelcomeControllerTest {

    public static final String USERNAME = "testUsername";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final String VIEW_AUTHORITY = "VIEW_WELCOME";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    User user;

    @Test
    @WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
    public void welcome_happyPath() throws Exception {

        String userFullName = "userFullName";
        String roleName = "roleName";

        when(userService.findByEmail(USERNAME)).thenReturn(user);
        when(user.getFullName()).thenReturn(userFullName);
        when(user.getRole()).thenReturn(new Role(roleName));

        mockMvc.perform(MockMvcRequestBuilders.get("/welcome")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("index/userIndex")).andExpect(model().attribute("userFullName", userFullName))
            .andExpect(model().attribute("role", roleName));

        verify(userService, times(1)).findByEmail(USERNAME);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = ROLE_ADMIN)
    public void welcome_withNoViewAuthority_thenAccessDenied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/welcome")).andExpect(status().is2xxSuccessful())
            .andExpect(model().attributeExists("exceptionMessage"));

    }
}
