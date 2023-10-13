package com.university.schedule.controller;

import com.university.schedule.model.User;
import com.university.schedule.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(value = AdminController.class)
@ActiveProfiles(value = "test")
public class AdminControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	UserService userService;

	@MockBean
	User user;

	public static final String USERNAME = "testUsername";

	public static final String PASSWORD = "testUserPassword";

	@Test
	public void getLogin_happyPath() throws Exception {
		mockMvc.perform(get("/admin/login"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("login/adminLogin"));
	}

	@Test
	@WithMockUser(username = USERNAME, password = PASSWORD, authorities = {"SOME"})
	public void getDashboard_happyPath() throws Exception {

		when(userService.findByEmail(USERNAME)).thenReturn(user);
		when(user.getFullName()).thenReturn("userFullName");

		mockMvc.perform(get("/admin/dashboard"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute("userFullName", "userFullName"))
				.andExpect(view().name("index/adminDashboard"));
	}


}
