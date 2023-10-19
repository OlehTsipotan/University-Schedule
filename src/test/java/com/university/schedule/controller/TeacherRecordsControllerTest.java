package com.university.schedule.controller;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.CourseService;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherRecordsController.class)
@ActiveProfiles("test")
public class TeacherRecordsControllerTest {

	public static final String USERNAME = "testUsername";
	public static final String VIEW_AUTHORITY = "VIEW_TEACHERS";
	public static final String EDIT_AUTHORITY = "EDIT_TEACHERS";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TeacherService teacherService;

	@MockBean
	private RoleService roleService;

	@MockBean
	private CourseService courseService;
	@Test
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenNoArgs_happyPath() throws Exception {
		List<TeacherDTO> teacherDTOList = new ArrayList<>();
		teacherDTOList.add(
				new TeacherDTO(1L, "teacher1@example.com", "John", "Doe", new RoleDTO(), true, new ArrayList<>()));
		teacherDTOList.add(
				new TeacherDTO(2L, "teacher2@example.com", "Jane", "Doe", new RoleDTO(), true, new ArrayList<>()));

		when(teacherService.findAllAsDTO(any())).thenReturn(teacherDTOList);

		mockMvc.perform(MockMvcRequestBuilders.get("/teachers?offset=5")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("teachers")).andExpect(
						model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
								"reverseSortDirection")).andExpect(model().attribute("entities", teacherDTOList));

		verify(teacherService, times(1)).findAllAsDTO(any());
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenLimitAndOffsetArgs_happyPath() throws Exception {
		List<TeacherDTO> teacherDTOList = new ArrayList<>();
		teacherDTOList.add(
				new TeacherDTO(1L, "teacher1@example.com", "John", "Doe", new RoleDTO(), true, new ArrayList<>()));
		teacherDTOList.add(
				new TeacherDTO(2L, "teacher2@example.com", "Jane", "Doe", new RoleDTO(), true, new ArrayList<>()));

		when(teacherService.findAllAsDTO(any())).thenReturn(teacherDTOList);

		mockMvc.perform(MockMvcRequestBuilders.get("/teachers?offset=10&limit=5"))
				.andExpect(status().is2xxSuccessful()).andExpect(view().name("teachers")).andExpect(
						model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
								"reverseSortDirection")).andExpect(model().attribute("entities", teacherDTOList));

		verify(teacherService, times(1)).findAllAsDTO(any());
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_happyPath() throws Exception {
		Long teacherId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/teachers/delete/{id}", teacherId))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/teachers"));

		verify(teacherService, times(1)).deleteById(teacherId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_whenTeacherServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
		Long teacherId = 1L;
		String exceptionMessage = "Delete Error";

		doThrow(new DeletionFailedException(exceptionMessage)).when(teacherService).deleteById(teacherId);

		mockMvc.perform(MockMvcRequestBuilders.get("/teachers/delete/{id}", teacherId))
				.andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(teacherService, times(1)).deleteById(teacherId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_happyPath() throws Exception {
		Long teacherId = 1L;
		TeacherDTO teacherDTO = new TeacherDTO(1L, "teacher1@example.com", "John", "Doe", new RoleDTO(), true, new ArrayList<>());

		when(teacherService.findByIdAsDTO(teacherId)).thenReturn(teacherDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/teachers/update/{id}", teacherId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(view().name("teachersUpdateForm"))
				.andExpect(model().attribute("entity", teacherDTO));

		verify(teacherService, times(1)).findByIdAsDTO(teacherId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_whenTeacherNotFoundTeacherServiceThrowsServiceException_thenProcessError()
			throws Exception {
		Long teacherId = 1L;
		String exceptionMessage = "Not found";

		when(teacherService.findByIdAsDTO(teacherId)).thenThrow(new ServiceException(exceptionMessage));

		mockMvc.perform(MockMvcRequestBuilders.get("/teachers/update/{id}", teacherId))
				.andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(teacherService, times(1)).findByIdAsDTO(teacherId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenValidTeacher_thenRedirectSuccess() throws Exception {
		Long teacherId = 1L;
		TeacherDTO teacherDTO = new TeacherDTO(teacherId, "teacher1@example.com", "John", "Doe", new RoleDTO(), true, new ArrayList<>());

		mockMvc.perform(MockMvcRequestBuilders.post("/teachers/update/{id}", teacherId).with(csrf())
						.flashAttr("teacherDTO", teacherDTO)).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/teachers/update/" + teacherId));

		verify(teacherService, times(1)).update(teacherDTO);
		verify(teacherService, times(0)).findByIdAsDTO(anyLong());
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenNotValidTeacherByEmptyField_thenProcessForm() throws Exception {
		Long teacherId = 1L;
		TeacherDTO teacherDTO = new TeacherDTO(teacherId, "teacher1@example.com", "", "Doe", new RoleDTO(), true, new ArrayList<>());

		when(teacherService.findByIdAsDTO(teacherId)).thenReturn(teacherDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/teachers/update/{id}", teacherId).with(csrf())
						.flashAttr("teacherDTO", teacherDTO)).andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", teacherDTO))
				.andExpect(view().name("teachersUpdateForm"));

		verify(teacherService, times(0)).update(any());
		verify(teacherService, times(1)).findByIdAsDTO(teacherId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenTeacherServiceThrowValidationException_thenProcessForm() throws Exception {
		Long teacherId = 1L;
		TeacherDTO teacherDTO = new TeacherDTO(teacherId, "teacher1@example.com", "John", "Doe", new RoleDTO(), true, new ArrayList<>());

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(teacherService.update(teacherDTO)).thenThrow(validationException);
		when(teacherService.findByIdAsDTO(teacherId)).thenReturn(teacherDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/teachers/update/{id}", teacherId).with(csrf())
				.flashAttr("teacherDTO", teacherDTO)).andExpect(status().is3xxRedirection());

		verify(teacherService, times(1)).update(teacherDTO);
		verifyNoMoreInteractions(teacherService);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenTeacherServiceThrowServiceException_thenProcessError() throws Exception {
		Long teacherId = 1L;
		TeacherDTO teacherDTO = new TeacherDTO(teacherId, "teacher1@example.com", "John", "Doe", new RoleDTO(), true, new ArrayList<>());

		String exceptionMessage = "Service Exception";
		ServiceException serviceException = new ServiceException(exceptionMessage);

		when(teacherService.update(teacherDTO)).thenThrow(serviceException);
		when(teacherService.findByIdAsDTO(teacherId)).thenReturn(teacherDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/teachers/update/{id}", teacherId).with(csrf())
						.flashAttr("teacherDTO", teacherDTO)).andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(teacherService, times(1)).update(teacherDTO);
		verifyNoMoreInteractions(teacherService);
	}
}

