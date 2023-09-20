package com.university.schedule.controller;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.CourseService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CourseRecordsController.class)
public class CourseRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CourseService courseService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_COURSES"})
	public void getAll_processPage() throws Exception {
		List<CourseDTO> courses = new ArrayList<>();
		courses.add(new CourseDTO(1L, "Course 1"));
		courses.add(new CourseDTO(2L, "Course 2"));

		when(courseService.findAllAsDTO(any(Pageable.class))).thenReturn(courses);

		mockMvc.perform(get("/courses")).andExpect(status().isOk()).andExpect(view().name("courses"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", courses));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_COURSES"})
	public void delete() throws Exception {
		Long courseId = 1L;

		mockMvc.perform(get("/courses/delete/{id}", courseId)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/courses"));

		verify(courseService, times(1)).deleteById(courseId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_COURSES"})
	public void getUpdateForm() throws Exception {
		Long courseId = 1L;
		CourseDTO courseDTO = new CourseDTO(courseId, "Course 1");

		when(courseService.findByIdAsDTO(courseId)).thenReturn(courseDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/courses/update/{id}", courseId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(view().name("coursesUpdateForm"))
				.andExpect(model().attribute("entity", courseDTO));

		verify(courseService, times(1)).findByIdAsDTO(courseId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_COURSES"})
	public void update_whenNotValidCourse_emptyField_thenProcessForm() throws Exception {
		Long courseId = 1L;

		CourseDTO courseDTO = new CourseDTO(courseId, ""); // Empty name

		when(courseService.findByIdAsDTO(courseId)).thenReturn(courseDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/courses/update/{id}", courseId).with(csrf())
						.flashAttr("courseDTO", courseDTO)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", courseDTO))
				.andExpect(view().name("coursesUpdateForm"));

	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_COURSES"})
	public void update_whenCourseServiceThrowValidationException_thenProcessForm() throws Exception {
		Long courseId = 1L;

		CourseDTO courseDTO = new CourseDTO(courseId, "Course 1");

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(courseService.save((CourseDTO) any())).thenThrow(validationException);
		when(courseService.findByIdAsDTO(courseId)).thenReturn(courseDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/courses/update/{id}", courseId).with(csrf())
				.flashAttr("courseDTO", courseDTO)).andExpect(status().is3xxRedirection());

		verify(courseService, times(1)).save(courseDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_COURSES"})
	public void update_whenCourseServiceThrowServiceException_thenProcessForm() throws Exception {
		Long courseId = 1L;

		CourseDTO courseDTO = new CourseDTO(courseId, "Course 1");

		ServiceException serviceException = new ServiceException("testException");

		when(courseService.save((CourseDTO) any())).thenThrow(serviceException);
		when(courseService.findByIdAsDTO(courseId)).thenReturn(courseDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/courses/update/{id}", courseId).with(csrf())
				.flashAttr("courseDTO", courseDTO)).andExpect(status().is3xxRedirection());

		verify(courseService, times(1)).save(courseDTO);
	}
}
