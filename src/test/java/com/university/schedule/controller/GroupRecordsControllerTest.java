package com.university.schedule.controller;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.CourseService;
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
	private CourseService courseService;
	@MockBean
	private DisciplineService disciplineService;

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_GROUPS"})
	public void getAll_processPage() throws Exception {
		List<GroupDTO> groupDTOs = new ArrayList<>();

		CourseDTO courseDTO = new CourseDTO(1L, "courseName");
		List<CourseDTO> courseDTOList = List.of(courseDTO);
		DisciplineDTO disciplineDTO = new DisciplineDTO(1L, "disciplineName");

		groupDTOs.add(new GroupDTO(1L, "Group 1", disciplineDTO, courseDTOList));
		groupDTOs.add(new GroupDTO(2L, "Group 2", disciplineDTO, courseDTOList));

		when(groupService.findAllAsDTO(any(Pageable.class))).thenReturn(groupDTOs);

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
		Long groupDTOId = 1L;
		CourseDTO courseDTO = new CourseDTO(1L, "courseName");
		List<CourseDTO> courseDTOList = List.of(courseDTO);
		DisciplineDTO disciplineDTO = new DisciplineDTO(1L, "disciplineName");
		List<DisciplineDTO> disciplineDTOList = List.of(disciplineDTO);

		GroupDTO groupDTO = new GroupDTO(groupDTOId, "Group 2", disciplineDTO, courseDTOList);

		when(groupService.findByIdAsDTO(groupDTOId)).thenReturn(groupDTO);
		when(disciplineService.findAllAsDTO()).thenReturn(disciplineDTOList);

		mockMvc.perform(MockMvcRequestBuilders.get("/groups/update/{id}", groupDTOId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity", "disciplineDTOList"))
				.andExpect(view().name("groupsUpdateForm")).andExpect(model().attribute("entity", groupDTO))
				.andExpect(model().attribute("disciplineDTOList", disciplineDTOList));

		verify(groupService, times(1)).findByIdAsDTO(groupDTOId);
		verify(disciplineService, times(1)).findAllAsDTO();
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_GROUPS"})
	public void update_whenNotValidGroup_emptyField_thenProcessForm() throws Exception {
		Long groupId = 1L;

		DisciplineDTO disciplineDTO1 = new DisciplineDTO(1L, "Discipline 1");
		DisciplineDTO disciplineDTO2 = new DisciplineDTO(2L, "Discipline 2");

		List<DisciplineDTO> disciplineDTOList = List.of(disciplineDTO1, disciplineDTO2);

		CourseDTO courseDTO = new CourseDTO(1L, "courseName");
		List<CourseDTO> courseDTOList = List.of(courseDTO);

		GroupDTO groupDTO = new GroupDTO(groupId, "", disciplineDTO1, courseDTOList); // Empty name

		when(groupService.findByIdAsDTO(groupId)).thenReturn(groupDTO);
		when(disciplineService.findAllAsDTO()).thenReturn(disciplineDTOList);

		mockMvc.perform(MockMvcRequestBuilders.post("/groups/update/{id}", groupId).with(csrf())
						.flashAttr("groupDTO", groupDTO)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", groupDTO))
				.andExpect(view().name("groupsUpdateForm"));

		verify(groupService, times(0)).save(groupDTO);

	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_GROUPS"})
	public void update_whenGroupServiceThrowValidationException_thenProcessForm() throws Exception {
		Long groupId = 1L;

		DisciplineDTO disciplineDTO1 = new DisciplineDTO(1L, "Discipline 1");
		DisciplineDTO disciplineDTO2 = new DisciplineDTO(2L, "Discipline 2");

		List<DisciplineDTO> disciplineDTOList = List.of(disciplineDTO1, disciplineDTO2);

		CourseDTO courseDTO = new CourseDTO(1L, "courseName");
		List<CourseDTO> courseDTOList = List.of(courseDTO);

		GroupDTO groupDTO = new GroupDTO(groupId, "GroupDTOName", disciplineDTO1, courseDTOList);

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(groupService.save((GroupDTO) any())).thenThrow(validationException);
		when(groupService.findByIdAsDTO(groupId)).thenReturn(groupDTO);
		when(disciplineService.findAllAsDTO()).thenReturn(disciplineDTOList);

		mockMvc.perform(MockMvcRequestBuilders.post("/groups/update/{id}", groupId).with(csrf())
				.flashAttr("groupDTO", groupDTO)).andExpect(status().is3xxRedirection());

		verify(groupService, times(1)).save(groupDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_GROUPS"})
	public void update_whenGroupServiceThrowServiceException_thenProcessForm() throws Exception {
		Long groupId = 1L;

		DisciplineDTO disciplineDTO1 = new DisciplineDTO(1L, "Discipline 1");
		DisciplineDTO disciplineDTO2 = new DisciplineDTO(2L, "Discipline 2");

		List<DisciplineDTO> disciplineDTOList = List.of(disciplineDTO1, disciplineDTO2);

		CourseDTO courseDTO = new CourseDTO(1L, "courseName");
		List<CourseDTO> courseDTOList = List.of(courseDTO);

		GroupDTO groupDTO = new GroupDTO(groupId, "GroupDTOName", disciplineDTO1, courseDTOList);

		ServiceException serviceException = new ServiceException("testException");

		when(groupService.save((GroupDTO) any())).thenThrow(serviceException);
		when(groupService.findByIdAsDTO(groupId)).thenReturn(groupDTO);
		when(disciplineService.findAllAsDTO()).thenReturn(disciplineDTOList);

		mockMvc.perform(MockMvcRequestBuilders.post("/groups/update/{id}", groupId).with(csrf())
				.flashAttr("groupDTO", groupDTO)).andExpect(status().is3xxRedirection());
		verify(groupService, times(1)).save(groupDTO);
	}
}
