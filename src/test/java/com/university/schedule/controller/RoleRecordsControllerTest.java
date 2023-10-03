package com.university.schedule.controller;

import com.university.schedule.dto.*;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Role;
import com.university.schedule.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
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

@WebMvcTest(RoleRecordsController.class)
@ComponentScan("com.university.schedule.formatter")
public class RoleRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RoleService roleService;

	@MockBean
	private AuthorityService authorityService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_ROLES"})
	public void getAll_processPage() throws Exception {
		List<RoleDTO> roleDTOS = new ArrayList<>();

		AuthorityDTO authorityDTO = new AuthorityDTO(1L, "authorityName");
		List<AuthorityDTO> authorityDTOList = List.of(authorityDTO);

		roleDTOS.add(new RoleDTO(1L, "role 1", authorityDTOList));
		roleDTOS.add(new RoleDTO(2L, "role 2", authorityDTOList));

		when(roleService.findAllAsDTO(any(Pageable.class))).thenReturn(roleDTOS);

		mockMvc.perform(MockMvcRequestBuilders.get("/roles")).andExpect(status().isOk())
				.andExpect(view().name("roles"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", roleDTOS));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_ROLES"})
	public void delete() throws Exception {
		Long groupId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/roles/delete/{id}", groupId))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/roles"));

		verify(roleService, times(1)).deleteById(groupId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_ROLES"})
	public void getUpdateForm() throws Exception {
		Long roleDTOid = 1L;
		AuthorityDTO authorityDTO = new AuthorityDTO(1L, "authorityName");
		List<AuthorityDTO> authorityDTOList = List.of(authorityDTO);

		RoleDTO roleDTO = new RoleDTO(roleDTOid, "role 1", authorityDTOList);

		when(roleService.findByIdAsDTO(roleDTOid)).thenReturn(roleDTO);
		when(authorityService.findAllAsDTO()).thenReturn(authorityDTOList);

		mockMvc.perform(MockMvcRequestBuilders.get("/roles/update/{id}", roleDTOid)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity", "authorityDTOList"))
				.andExpect(view().name("rolesUpdateForm")).andExpect(model().attribute("entity", roleDTO))
				.andExpect(model().attribute("authorityDTOList", authorityDTOList));

		verify(roleService, times(1)).findByIdAsDTO(roleDTOid);
		verify(authorityService, times(1)).findAllAsDTO();
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_ROLES"})
	public void update_whenNotValidGroup_emptyField_thenProcessForm() throws Exception {
		Long roleDTOid = 1L;
		AuthorityDTO authorityDTO = new AuthorityDTO(1L, "authorityName");
		List<AuthorityDTO> authorityDTOList = List.of(authorityDTO);

		RoleDTO roleDTO = new RoleDTO(roleDTOid, "", authorityDTOList);

		when(roleService.findByIdAsDTO(roleDTOid)).thenReturn(roleDTO);
		when(authorityService.findAllAsDTO()).thenReturn(authorityDTOList);

		mockMvc.perform(MockMvcRequestBuilders.post("/roles/update/{id}", roleDTOid).with(csrf())
						.flashAttr("roleDTO", roleDTO)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", roleDTO))
				.andExpect(view().name("rolesUpdateForm"));

		verify(roleService, times(0)).save(roleDTO);

	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_ROLES"})
	public void update_whenGroupServiceThrowValidationException_thenProcessForm() throws Exception {
		Long roleDTOid = 1L;
		AuthorityDTO authorityDTO = new AuthorityDTO(1L, "authorityName");
		List<AuthorityDTO> authorityDTOList = List.of(authorityDTO);

		RoleDTO roleDTO = new RoleDTO(roleDTOid, "role 1", authorityDTOList);

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(roleService.save((RoleDTO) any())).thenThrow(validationException);
		when(roleService.findByIdAsDTO(roleDTOid)).thenReturn(roleDTO);
		when(authorityService.findAllAsDTO()).thenReturn(authorityDTOList);

		mockMvc.perform(MockMvcRequestBuilders.post("/roles/update/{id}", roleDTOid).with(csrf())
				.flashAttr("roleDTO", roleDTO)).andExpect(status().is3xxRedirection());

		verify(roleService, times(1)).save(roleDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_ROLES"})
	public void update_whenGroupServiceThrowServiceException_thenProcessForm() throws Exception {
		Long roleDTOid = 1L;
		AuthorityDTO authorityDTO = new AuthorityDTO(1L, "authorityName");
		List<AuthorityDTO> authorityDTOList = List.of(authorityDTO);

		RoleDTO roleDTO = new RoleDTO(roleDTOid, "role 1", authorityDTOList);

		ServiceException serviceException = new ServiceException("testException");

		when(roleService.save((RoleDTO) any())).thenThrow(serviceException);
		when(roleService.findByIdAsDTO(roleDTOid)).thenReturn(roleDTO);
		when(authorityService.findAllAsDTO()).thenReturn(authorityDTOList);

		mockMvc.perform(MockMvcRequestBuilders.post("/roles/update/{id}", roleDTOid).with(csrf())
				.flashAttr("roleDTO", roleDTO)).andExpect(status().is3xxRedirection());

		verify(roleService, times(1)).save(roleDTO);
	}
}
