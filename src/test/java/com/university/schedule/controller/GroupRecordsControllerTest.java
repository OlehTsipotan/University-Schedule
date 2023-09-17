package com.university.schedule.controller;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import com.university.schedule.service.DisciplineService;
import com.university.schedule.service.GroupService;
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

@WebMvcTest(GroupRecordsController.class)
public class GroupRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GroupService groupService;

	@MockBean
	private DisciplineService disciplineService;

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_GROUPS"})
	public void getAll_processPage() throws Exception {
		List<GroupDTO> groupDTOs = new ArrayList<>();

		String disciplineName = "Discipline 1";
		List<String> courseNameList = List.of("Course 1", "Course 2");

		groupDTOs.add(new GroupDTO(1L, "Group 1", disciplineName, courseNameList));
		groupDTOs.add(new GroupDTO(2L, "Group 2", disciplineName, courseNameList));

		when(groupDTOService.findAll(any(Pageable.class))).thenReturn(groupDTOs);

		mockMvc.perform(MockMvcRequestBuilders.get("/groups")).andExpect(status().isOk())
				.andExpect(view().name("groups"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", groupDTOs));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_GROUPS"})
	public void delete() throws Exception {
		Long groupId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/groups/delete/{id}", groupId))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/groups"));

		verify(groupService, times(1)).deleteById(groupId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_GROUPS"})
	public void getUpdateForm() throws Exception {
		Long groupId = 1L;

		Discipline discipline1 = new Discipline(1L, "Discipline 1");
		Discipline discipline2 = new Discipline(2L, "Discipline 2");

		List<Discipline> disciplines = new ArrayList<>(List.of(discipline1, discipline2));

		Group group = new Group(groupId, "Group 1", discipline1);

		when(groupService.findById(groupId)).thenReturn(group);
		when(disciplineService.findAll()).thenReturn(disciplines);

		mockMvc.perform(MockMvcRequestBuilders.get("/groups/update/{id}", groupId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity", "disciplines")).andExpect(view().name("groupsUpdateForm"))
				.andExpect(model().attribute("entity", group)).andExpect(model().attribute("disciplines", disciplines));

		verify(groupService, times(1)).findById(groupId);
		verify(disciplineService, times(1)).findAll();
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_GROUPS"})
	public void update_whenNotValidGroup_emptyField_thenProcessForm() throws Exception {
		Long groupId = 1L;

		Discipline discipline1 = new Discipline(1L, "Discipline 1");
		Discipline discipline2 = new Discipline(2L, "Discipline 2");

		List<Discipline> disciplines = new ArrayList<>(List.of(discipline1, discipline2));

		Group group = new Group(groupId, "", discipline1); // Empty name

		when(groupService.findById(groupId)).thenReturn(group);
		when(disciplineService.findAll()).thenReturn(disciplines);

		mockMvc.perform(
						MockMvcRequestBuilders.post("/groups/update/{id}", groupId).with(csrf()).flashAttr("group", group))
				.andExpect(status().isOk()).andExpect(model().attributeExists("entity"))
				.andExpect(model().attribute("entity", group)).andExpect(view().name("groupsUpdateForm"));

	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_GROUPS"})
	public void update_whenGroupServiceThrowValidationException_thenProcessForm() throws Exception {
		Long groupId = 1L;
		Discipline discipline1 = new Discipline(1L, "Discipline 1");
		Discipline discipline2 = new Discipline(2L, "Discipline 2");

		List<Discipline> disciplines = new ArrayList<>(List.of(discipline1, discipline2));

		Group group = new Group(groupId, "Group 1", discipline1);

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(groupService.save(any())).thenThrow(validationException);
		when(groupService.findById(groupId)).thenReturn(group);
		when(disciplineService.findAll()).thenReturn(disciplines);

		mockMvc.perform(
						MockMvcRequestBuilders.post("/groups/update/{id}", groupId).with(csrf()).flashAttr("group", group))
				.andExpect(status().isOk()).andExpect(model().attributeExists("validationServiceErrors"))
				.andExpect(model().attribute("validationServiceErrors", validationException.getViolations()))
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", group))
				.andExpect(view().name("groupsUpdateForm"));

		verify(groupService, times(1)).save(group);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_GROUPS"})
	public void update_whenGroupServiceThrowServiceException_thenProcessForm() throws Exception {
		Long groupId = 1L;

		Discipline discipline1 = new Discipline(1L, "Discipline 1");
		Discipline discipline2 = new Discipline(2L, "Discipline 2");

		List<Discipline> disciplines = new ArrayList<>(List.of(discipline1, discipline2));

		Group group = new Group(groupId, "Group 1", discipline1);

		ServiceException serviceException = new ServiceException("testException");

		when(groupService.save(any())).thenThrow(serviceException);
		when(groupService.findById(groupId)).thenReturn(group);
		when(disciplineService.findAll()).thenReturn(disciplines);

		mockMvc.perform(
						MockMvcRequestBuilders.post("/groups/update/{id}", groupId).with(csrf()).flashAttr("group", group))
				.andExpect(status().isOk()).andExpect(model().attributeExists("serviceError"))
				.andExpect(model().attribute("serviceError", serviceException.getMessage()))
				.andExpect(model().attribute("entity", group)).andExpect(view().name("groupsUpdateForm"));
	}
}
