package com.university.schedule.controller;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.CourseService;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.TeacherService;
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

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherRecordsController.class)
public class TeacherRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TeacherService teacherService;

	@MockBean
	private RoleService roleService;

	@MockBean
	private CourseService courseService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_TEACHERS"})
	public void getAll_processPage() throws Exception {
		CourseDTO courseDTO = new CourseDTO(1L, "courseDTOName");
		List<CourseDTO> courseDTOS = List.of(courseDTO);
		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<TeacherDTO> teacherDTOS = new ArrayList<>();
		teacherDTOS.add(new TeacherDTO(1L, "email 1", "firstName 1", "lastName 1", roleDTO, true, courseDTOS));

		// Mock the behavior of teacherDTOService
		when(teacherService.findAllAsDTO(any(Pageable.class))).thenReturn(teacherDTOS);

		// Perform a GET request to /teachers and verify the result
		mockMvc.perform(MockMvcRequestBuilders.get("/teachers")).andExpect(status().isOk())
				.andExpect(view().name("teachers")).andExpect(
						model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
								"reverseSortDirection")).andExpect(model().attribute("entities", teacherDTOS));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_TEACHERS"})
	public void delete() throws Exception {
		Long teacherId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/teachers/delete/{id}", teacherId))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/teachers"));

		verify(teacherService, times(1)).deleteById(teacherId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_TEACHERS"})
	public void getUpdateForm() throws Exception {
		Long teacherId = 1L;

		CourseDTO courseDTO = new CourseDTO(1L, "courseDTOName");
		List<CourseDTO> courseDTOS = List.of(courseDTO);
		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		TeacherDTO teacherDTO =
				new TeacherDTO(teacherId, "email 1", "firstName 1", "lastName 1", roleDTO, true, courseDTOS);

		when(teacherService.findByIdAsDTO(teacherId)).thenReturn(teacherDTO);
		when(courseService.findAllAsDTO()).thenReturn(courseDTOS);
		when(roleService.findAllAsDTO()).thenReturn(roleDTOS);

		mockMvc.perform(MockMvcRequestBuilders.get("/teachers/update/{id}", teacherId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity", "courseDTOList", "roleDTOList"))
				.andExpect(view().name("teachersUpdateForm")).andExpect(model().attribute("entity", teacherDTO))
				.andExpect(model().attribute("courseDTOList", courseDTOS))
				.andExpect(model().attribute("roleDTOList", roleDTOS));

		verify(teacherService, times(1)).findByIdAsDTO(teacherId);
		verify(courseService, times(1)).findAllAsDTO();
		verify(roleService, times(1)).findAllAsDTO();
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_TEACHERS"})
	public void update_whenNotValidTeacherUpdateDTO_emptyField_thenProcessForm() throws Exception {
		Long teacherId = 1L;

		CourseDTO courseDTO = new CourseDTO(1L, "courseDTOName");
		List<CourseDTO> courseDTOS = List.of(courseDTO);
		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		TeacherDTO teacherDTO = new TeacherDTO(teacherId, "email 1", "firstName 1", "", roleDTO, true, courseDTOS);

		when(teacherService.findByIdAsDTO(teacherId)).thenReturn(teacherDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/teachers/update/{id}", teacherId)
						.param("isEnable", "false") // Add any other request parameters as needed
						.with(csrf()).flashAttr("teacherDTO", teacherDTO)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", teacherDTO))
				.andExpect(view().name("teachersUpdateForm"));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_TEACHERS"})
	public void update_whenTeacherUpdateDTOServiceThrowValidationException_thenProcessForm() throws Exception {
		Long teacherId = 1L;

		CourseDTO courseDTO = new CourseDTO(1L, "courseDTOName");
		List<CourseDTO> courseDTOS = List.of(courseDTO);
		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		TeacherDTO teacherDTO =
				new TeacherDTO(teacherId, "email 1", "firstName 1", "lastName 1", roleDTO, true, courseDTOS);

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(teacherService.save((TeacherDTO) any())).thenThrow(validationException);
		when(teacherService.findByIdAsDTO(teacherId)).thenReturn(teacherDTO);
		when(courseService.findByIdAsDTO(courseDTO.getId())).thenReturn(courseDTO);
		when(roleService.findByIdAsDTO(roleDTO.getId())).thenReturn(roleDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/teachers/update/{id}", teacherId)
				.param("isEnable", "false") // Add any other request parameters as needed
				.with(csrf()).flashAttr("teacherDTO", teacherDTO)).andExpect(status().is3xxRedirection());

		verify(teacherService, times(1)).save(teacherDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_TEACHERS"})
	public void update_whenTeacherUpdateDTOServiceThrowServiceException_thenProcessForm() throws Exception {
		Long teacherId = 1L;

		CourseDTO courseDTO = new CourseDTO(1L, "courseDTOName");
		List<CourseDTO> courseDTOS = List.of(courseDTO);
		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		TeacherDTO teacherDTO =
				new TeacherDTO(teacherId, "email 1", "firstName 1", "lastName 1", roleDTO, true, courseDTOS);

		ServiceException serviceException = new ServiceException("testException");

		when(teacherService.save((TeacherDTO) any())).thenThrow(serviceException);
		when(teacherService.findByIdAsDTO(teacherId)).thenReturn(teacherDTO);
		when(courseService.findByIdAsDTO(courseDTO.getId())).thenReturn(courseDTO);
		when(roleService.findByIdAsDTO(roleDTO.getId())).thenReturn(roleDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/teachers/update/{id}", teacherId)
				.param("isEnable", "false") // Add any other request parameters as needed
				.with(csrf()).flashAttr("teacherDTO", teacherDTO)).andExpect(status().is3xxRedirection());

		verify(teacherService, times(1)).save(teacherDTO);
	}
}
