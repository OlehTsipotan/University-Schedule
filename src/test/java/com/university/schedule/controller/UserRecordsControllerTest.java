package com.university.schedule.controller;

import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.UserDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRecordsController.class)
public class UserRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private RoleService roleService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_USERS"})
	public void getAll_processPage() throws Exception {
		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName", null);
		List<UserDTO> userDTOS = new ArrayList<>();
		userDTOS.add(new UserDTO(1L, "email 1", "firstName 1", "lastName 1", roleDTO, true));

		// Mock the behavior of userService
		when(userService.findAllAsDTO(any(Pageable.class))).thenReturn(userDTOS);

		// Perform a GET request to /users and verify the result
		mockMvc.perform(MockMvcRequestBuilders.get("/users")).andExpect(status().isOk()).andExpect(view().name("users"))
				.andExpect(model().attributeExists("entities", "currentLimit", "currentOffset", "sortField",
						"sortDirection", "reverseSortDirection")).andExpect(model().attribute("entities", userDTOS));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_USERS"})
	public void delete() throws Exception {
		Long userId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/users/delete/{id}", userId)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/users"));

		verify(userService, times(1)).deleteById(userId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_USERS"})
	public void getUpdateForm() throws Exception {
		Long userId = 1L;

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		UserDTO userDTO = new UserDTO(userId, "email 1", "firstName 1", "lastName 1", roleDTO, true);

		when(userService.findByIdAsDTO(userId)).thenReturn(userDTO);
		when(roleService.findAllAsDTO()).thenReturn(roleDTOS);

		mockMvc.perform(MockMvcRequestBuilders.get("/users/update/{id}", userId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity", "roleDTOList")).andExpect(view().name("usersUpdateForm"))
				.andExpect(model().attribute("entity", userDTO)).andExpect(model().attribute("roleDTOList", roleDTOS));

		verify(userService, times(1)).findByIdAsDTO(userId);
		verify(roleService, times(1)).findAllAsDTO();
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_USERS"})
	public void update_whenNotValidUserUpdateDTO_emptyField_thenProcessForm() throws Exception {
		Long userId = 1L;

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		UserDTO userDTO = new UserDTO(userId, "email 1", "firstName 1", "", roleDTO, true);

		when(userService.findByIdAsDTO(userId)).thenReturn(userDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/users/update/{id}", userId)
						.param("isEnable", "false") // Add any other request parameters as needed
						.with(csrf()).flashAttr("userDTO", userDTO)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", userDTO))
				.andExpect(view().name("usersUpdateForm"));

		verify(userService, times(0)).update(userDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_USERS"})
	public void update_whenUserUpdateDTOServiceThrowValidationException_thenProcessForm() throws Exception {
		Long userId = 1L;

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		UserDTO userDTO = new UserDTO(userId, "email 1", "firstName 1", "lastName 1", roleDTO, true);

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(userService.findByIdAsDTO(userId)).thenReturn(userDTO);
		when(userService.update((UserDTO) any())).thenThrow(validationException);

		mockMvc.perform(MockMvcRequestBuilders.post("/users/update/{id}", userId)
				.param("isEnable", "false") // Add any other request parameters as needed
				.with(csrf()).flashAttr("userDTO", userDTO)).andExpect(status().is3xxRedirection());

		verify(userService, times(1)).update(userDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_USERS"})
	public void update_whenUserUpdateDTOServiceThrowServiceException_thenProcessForm() throws Exception {
		Long userId = 1L;

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		UserDTO userDTO = new UserDTO(userId, "email 1", "firstName 1", "lastName 1", roleDTO, true);

		ServiceException serviceException = new ServiceException("testException");

		when(userService.update((UserDTO) any())).thenThrow(serviceException);
		when(userService.findByIdAsDTO(userId)).thenReturn(userDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/users/update/{id}", userId)
				.param("isEnable", "false") // Add any other request parameters as needed
				.with(csrf()).flashAttr("userDTO", userDTO)).andExpect(status().is3xxRedirection());
	}
}
