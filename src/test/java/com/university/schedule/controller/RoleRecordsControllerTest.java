package com.university.schedule.controller;

import com.university.schedule.config.WebTestConfig;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.AuthorityService;
import com.university.schedule.service.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleRecordsController.class)
@Import(WebTestConfig.class)
@ActiveProfiles("test")
public class RoleRecordsControllerTest {

	private static final String USERNAME = "testUsername";
	private static final String VIEW_AUTHORITY = "VIEW_ROLES";
	private static final String EDIT_AUTHORITY = "EDIT_ROLES";
	private static final String INSERT_AUTHORITY = "INSERT_ROLES";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RoleService roleService;

	@MockBean
	private AuthorityService authorityService;

	@Test
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenNoArgs_happyPath() throws Exception {
		// Arrange
		List<RoleDTO> roleDTOList = new ArrayList<>();
		roleDTOList.add(new RoleDTO(1L, "Role A"));
		roleDTOList.add(new RoleDTO(2L, "Role B"));

		when(roleService.findAllAsDTO(any())).thenReturn(roleDTOList);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/roles")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("roles")).andExpect(
						model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
								"reverseSortDirection")).andExpect(model().attribute("entities", roleDTOList));

		verify(roleService, times(1)).findAllAsDTO(any());
	}


	@ParameterizedTest
	@CsvSource({"5, 10"})
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenLimitAndOffsetArgs_happyPath(int limit, int offset) throws Exception {
		// Arrange
		List<RoleDTO> roleDTOList = new ArrayList<>();
		roleDTOList.add(new RoleDTO(1L, "Role A"));
		roleDTOList.add(new RoleDTO(2L, "Role B"));

		when(roleService.findAllAsDTO(
				argThat(pageable -> pageable.getOffset() == offset && pageable.getPageSize() == limit))).thenReturn(
				roleDTOList);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get(String.format("/roles?offset=%d&limit=%d", offset, limit)))
				.andExpect(status().is2xxSuccessful()).andExpect(view().name("roles")).andExpect(
						model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
								"reverseSortDirection")).andExpect(model().attribute("entities", roleDTOList));

		verify(roleService, times(1)).findAllAsDTO(
				argThat(pageable -> pageable.getOffset() == offset && pageable.getPageSize() == limit));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_happyPath() throws Exception {
		// Arrange
		Long roleId = 1L;

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/roles/delete/{id}", roleId)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/roles"));

		verify(roleService, times(1)).deleteById(roleId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_whenRoleServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
		// Arrange
		Long roleId = 1L;
		String exceptionMessage = "Deletion Failed";

		doThrow(new DeletionFailedException(exceptionMessage)).when(roleService).deleteById(roleId);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/roles/delete/{id}", roleId)).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("error")).andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(roleService, times(1)).deleteById(roleId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_happyPath() throws Exception {
		// Arrange
		Long roleId = 1L;
		RoleDTO roleDTO = new RoleDTO(roleId, "Role Name");

		when(roleService.findByIdAsDTO(roleId)).thenReturn(roleDTO);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/roles/update/{id}", roleId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity", "authorityDTOList"))
				.andExpect(model().attribute("entity", roleDTO)).andExpect(view().name("rolesUpdateForm"));

		verify(roleService, times(1)).findByIdAsDTO(roleId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_whenRoleServiceThrowsServiceException_thenProcessError() throws Exception {
		// Arrange
		Long roleId = 1L;
		String exceptionMessage = "Service Exception";

		when(roleService.findByIdAsDTO(roleId)).thenThrow(new ServiceException(exceptionMessage));

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/roles/update/{id}", roleId)).andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("exceptionMessage"))
				.andExpect(model().attribute("exceptionMessage", exceptionMessage)).andExpect(view().name("error"));

		verify(roleService, times(1)).findByIdAsDTO(roleId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenValidRole_thenRedirectSuccess() throws Exception {
		// Arrange
		Long roleId = 1L;
		RoleDTO validRoleDTO = RoleDTO.builder().id(roleId).name("Valid Role").build();

		// Act and Assert
		mockMvc.perform(post("/roles/update/{id}", roleId).with(csrf()).flashAttr("roleDTO", validRoleDTO))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/roles/update/" + roleId));

		verify(roleService, times(1)).save(validRoleDTO);
		verify(roleService, times(0)).findByIdAsDTO(any());
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenNotValidRoleByEmptyField_thenProcessForm() throws Exception {
		// Arrange
		Long roleId = 1L;
		RoleDTO invalidRoleDTO = RoleDTO.builder().id(roleId).name("").build();
		when(roleService.findByIdAsDTO(roleId)).thenReturn(invalidRoleDTO);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/roles/update/{id}", roleId).with(csrf())
						.flashAttr("roleDTO", invalidRoleDTO)).andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", invalidRoleDTO))
				.andExpect(view().name("rolesUpdateForm"));

		// Verify that roleService.save(roleDTO) was not called
		verify(roleService, times(0)).save(invalidRoleDTO);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenRoleServiceThrowValidationException_thenProcessForm() throws Exception {
		// Arrange
		Long roleId = 1L;
		RoleDTO roleDTO =
				RoleDTO.builder().id(roleId).name("Valid Name").authorityDTOS(Collections.emptyList()).build();

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(roleService.save(roleDTO)).thenThrow(validationException);

		// Act and Assert
		mockMvc.perform(
						MockMvcRequestBuilders.post("/roles/update/{id}", roleId).with(csrf()).flashAttr("roleDTO", roleDTO))
				.andExpect(status().is3xxRedirection());

		verify(roleService, times(1)).save(roleDTO);
		verify(roleService, times(0)).findByIdAsDTO(anyLong());
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenRoleServiceThrowServiceException_processError() throws Exception {
		Long roleId = 1L;
		RoleDTO roleDTO =
				RoleDTO.builder().id(roleId).name("Valid Name").authorityDTOS(Collections.emptyList()).build();
		String exceptionMessage = "Service Exception";

		when(roleService.save(any(RoleDTO.class))).thenThrow(new ServiceException(exceptionMessage));

		mockMvc.perform(
						MockMvcRequestBuilders.post("/roles/update/{id}", roleId).with(csrf()).flashAttr("roleDTO", roleDTO))
				.andExpect(status().isOk()).andExpect(model().attribute("exceptionMessage", exceptionMessage))
				.andExpect(view().name("error"));

		verify(roleService, times(1)).save(any(RoleDTO.class));
		verify(roleService, times(0)).findByIdAsDTO(any());
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void getInsertForm_happyPath() throws Exception {
		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/roles/insert")).andExpect(status().isOk())
				.andExpect(view().name("rolesInsertForm")).andExpect(model().attributeExists("authorityDTOList"));

	}


	@Test
	@WithMockUser(username = USERNAME)
	public void getInsertFrom_whenAccessDenied_processError() throws Exception {
		mockMvc.perform(get("/roles/insert")).andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("exceptionMessage")).andExpect(view().name("error"));

	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenValidRole_thenRedirectSuccess() throws Exception {
		// Arrange
		Long roleId = 0L;
		RoleDTO roleDTO = RoleDTO.builder().name("Role A").build();

		when(roleService.save(roleDTO)).thenReturn(roleId);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/roles/insert").with(csrf()).flashAttr("roleDTO", roleDTO))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/authorities"))
				.andExpect(flash().attribute("insertedSuccessId", roleId));

		// Verify interactions and ensure no more interactions
		verify(roleService, times(1)).save(roleDTO);
		verifyNoMoreInteractions(roleService);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenNotValidRoleByEmptyField_thenProcessForm() throws Exception {
		// Arrange
		RoleDTO roleDTO = RoleDTO.builder().name("").build();

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/roles/insert").with(csrf()).flashAttr("roleDTO", roleDTO))
				.andExpect(status().is2xxSuccessful()).andExpect(view().name("rolesInsertForm"));

		// Verify that roleService was not interacted with
		verifyNoInteractions(roleService);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenRoleServiceThrowValidationException_thenProcessForm() throws Exception {
		// Arrange
		RoleDTO roleDTO = RoleDTO.builder().name("Role A").build();

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(roleService.save(roleDTO)).thenThrow(validationException);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/roles/insert").with(csrf()).flashAttr("roleDTO", roleDTO))
				.andExpect(status().is3xxRedirection());

		// Verify that roleService.save(roleDTO) was called once
		verify(roleService, times(1)).save(roleDTO);
		verifyNoMoreInteractions(roleService);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenRoleServiceThrowServiceException_processError() throws Exception {
		// Arrange
		RoleDTO roleDTO = RoleDTO.builder().name("Role A").build();
		String exceptionMessage = "Service Exception";

		when(roleService.save(roleDTO)).thenThrow(new ServiceException(exceptionMessage));

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/roles/insert").with(csrf()).flashAttr("roleDTO", roleDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		// Verify that roleService.save(roleDTO) was called once
		verify(roleService, times(1)).save(roleDTO);
		verifyNoMoreInteractions(roleService);
	}
}
