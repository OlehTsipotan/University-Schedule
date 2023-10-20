package com.university.schedule.controller;

import com.university.schedule.config.WebTestConfig;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.UserDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.RoleService;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRecordsController.class)
@Import(WebTestConfig.class)
@ActiveProfiles("test")
public class UserRecordsControllerTest {

	public static final String USERNAME = "testUsername";
	public static final String VIEW_AUTHORITY = "VIEW_USERS";
	public static final String EDIT_AUTHORITY = "EDIT_USERS";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private RoleService roleService;

	@Test
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenNoArgs_happyPath() throws Exception {
		List<UserDTO> userDTOList = new ArrayList<>();
		userDTOList.add(new UserDTO(1L, "user1@example.com", "John", "Doe", new RoleDTO(), true));
		userDTOList.add(new UserDTO(2L, "user2@example.com", "Jane", "Doe", new RoleDTO(), true));

		when(userService.findAllAsDTO(any())).thenReturn(userDTOList);

		mockMvc.perform(MockMvcRequestBuilders.get("/users?offset=5")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("users")).andExpect(
						model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
								"reverseSortDirection")).andExpect(model().attribute("entities", userDTOList));

		verify(userService, times(1)).findAllAsDTO(any());
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenLimitAndOffsetArgs_happyPath() throws Exception {
		List<UserDTO> userDTOList = new ArrayList<>();
		userDTOList.add(new UserDTO(1L, "user1@example.com", "John", "Doe", new RoleDTO(), true));
		userDTOList.add(new UserDTO(2L, "user2@example.com", "Jane", "Doe", new RoleDTO(), true));

		when(userService.findAllAsDTO(any())).thenReturn(userDTOList);

		mockMvc.perform(MockMvcRequestBuilders.get("/users?offset=10&limit=5")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("users")).andExpect(
						model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
								"reverseSortDirection")).andExpect(model().attribute("entities", userDTOList));

		verify(userService, times(1)).findAllAsDTO(any());
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_happyPath() throws Exception {
		Long userId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/users/delete/{id}", userId)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/users"));

		verify(userService, times(1)).deleteById(userId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_whenUserServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
		Long userId = 1L;
		String exceptionMessage = "Delete Error";

		doThrow(new DeletionFailedException(exceptionMessage)).when(userService).deleteById(userId);

		mockMvc.perform(MockMvcRequestBuilders.get("/users/delete/{id}", userId)).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("error")).andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(userService, times(1)).deleteById(userId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_happyPath() throws Exception {
		Long userId = 1L;
		UserDTO userDTO = new UserDTO(userId, "user1@example.com", "John", "Doe", new RoleDTO(), true);

		when(userService.findByIdAsDTO(userId)).thenReturn(userDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/users/update/{id}", userId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(view().name("usersUpdateForm"))
				.andExpect(model().attribute("entity", userDTO));

		verify(userService, times(1)).findByIdAsDTO(userId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_whenUserNotFoundUserServiceThrowsServiceException_thenProcessError() throws Exception {
		Long userId = 1L;
		String exceptionMessage = "Not found";

		when(userService.findByIdAsDTO(userId)).thenThrow(new ServiceException(exceptionMessage));

		mockMvc.perform(MockMvcRequestBuilders.get("/users/update/{id}", userId)).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("error")).andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(userService, times(1)).findByIdAsDTO(userId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenValidUser_thenRedirectSuccess() throws Exception {
		Long userId = 1L;
		UserDTO userDTO = new UserDTO(userId, "user1@example.com", "John", "Doe", new RoleDTO(), true);

		mockMvc.perform(
						MockMvcRequestBuilders.post("/users/update/{id}", userId).with(csrf()).flashAttr("userDTO", userDTO))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/users/update/" + userId));

		verify(userService, times(1)).update(userDTO);
		verify(userService, times(0)).findByIdAsDTO(anyLong());
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenNotValidUserByEmptyField_thenProcessForm() throws Exception {
		Long userId = 1L;
		UserDTO userDTO = new UserDTO(userId, "user1@example.com", "", "Doe", new RoleDTO(), true);

		when(userService.findByIdAsDTO(userId)).thenReturn(userDTO);

		mockMvc.perform(
						MockMvcRequestBuilders.post("/users/update/{id}", userId).with(csrf()).flashAttr("userDTO", userDTO))
				.andExpect(status().is2xxSuccessful()).andExpect(model().attributeExists("entity"))
				.andExpect(model().attribute("entity", userDTO)).andExpect(view().name("usersUpdateForm"));

		verify(userService, times(0)).update(any());
		verify(userService, times(1)).findByIdAsDTO(userId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenUserServiceThrowValidationException_thenProcessForm() throws Exception {
		Long userId = 1L;
		UserDTO userDTO = new UserDTO(userId, "user1@example.com", "John", "Doe", new RoleDTO(), true);

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(userService.update(userDTO)).thenThrow(validationException);
		when(userService.findByIdAsDTO(userId)).thenReturn(userDTO);

		mockMvc.perform(
						MockMvcRequestBuilders.post("/users/update/{id}", userId).with(csrf()).flashAttr("userDTO", userDTO))
				.andExpect(status().is3xxRedirection());

		verify(userService, times(1)).update(userDTO);
		verifyNoMoreInteractions(userService);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenUserServiceThrowServiceException_thenProcessError() throws Exception {
		Long userId = 1L;
		UserDTO userDTO = new UserDTO(userId, "user1@example.com", "John", "Doe", new RoleDTO(), true);

		String exceptionMessage = "Service Exception";
		ServiceException serviceException = new ServiceException(exceptionMessage);

		when(userService.update(userDTO)).thenThrow(serviceException);
		when(userService.findByIdAsDTO(userId)).thenReturn(userDTO);

		mockMvc.perform(
						MockMvcRequestBuilders.post("/users/update/{id}", userId).with(csrf()).flashAttr("userDTO", userDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(userService, times(1)).update(userDTO);
		verifyNoMoreInteractions(userService);
	}
}
