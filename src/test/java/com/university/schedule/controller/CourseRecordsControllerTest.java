package com.university.schedule.controller;

import com.university.schedule.config.WebTestConfig;
import com.university.schedule.dto.CourseDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.CourseService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseRecordsController.class)
@Import(WebTestConfig.class)
@ActiveProfiles("test")
public class CourseRecordsControllerTest {

	private static final String USERNAME = "testUsername";
	private static final String VIEW_AUTHORITY = "VIEW_COURSES";
	private static final String EDIT_AUTHORITY = "EDIT_COURSES";
	private static final String INSERT_AUTHORITY = "INSERT_COURSES";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CourseService courseService;

	@Test
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenNoArgs_happyPath() throws Exception {
		List<CourseDTO> courseList = new ArrayList<>();
		courseList.add(new CourseDTO(1L, "Course A"));
		courseList.add(new CourseDTO(2L, "Course B"));

		when(courseService.findAllAsDTO(any(), any())).thenReturn(courseList);

		mockMvc.perform(MockMvcRequestBuilders.get("/courses")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("courses"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", courseList));

		verify(courseService, times(1)).findAllAsDTO(any(), any());
	}


	@ParameterizedTest
	@CsvSource({"5, 10"})
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenLimitAndOffsetArgs_happyPath(int limit, int offset) throws Exception {
		List<CourseDTO> courseList = new ArrayList<>();
		courseList.add(new CourseDTO(1L, "Course A"));
		courseList.add(new CourseDTO(2L, "Course B"));

		when(courseService.findAllAsDTO(any(), any())).thenReturn(courseList);

		mockMvc.perform(MockMvcRequestBuilders.get(String.format("/courses?offset=%d&limit=%d", offset, limit)))
				.andExpect(status().is2xxSuccessful()).andExpect(view().name("courses"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", courseList));

		verify(courseService, times(1)).findAllAsDTO(argThat(str -> str.equals(USERNAME)),
				argThat(pageable -> pageable.getOffset() == offset && pageable.getPageSize() == limit));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_happyPath() throws Exception {
		Long courseId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/courses/delete/{id}", courseId))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/courses"));

		verify(courseService, times(1)).deleteById(courseId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_whenCourseServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
		Long courseId = 1L;
		String exceptionMessage = "Delete Error";

		doThrow(new DeletionFailedException(exceptionMessage)).when(courseService).deleteById(courseId);

		mockMvc.perform(MockMvcRequestBuilders.get("/courses/delete/{id}", courseId))
				.andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(courseService, times(1)).deleteById(courseId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_happyPath() throws Exception {
		Long courseId = 1L;
		CourseDTO courseDTO = new CourseDTO(1L, "Course A");

		when(courseService.findByIdAsDTO(courseId)).thenReturn(courseDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/courses/update/{id}", courseId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(view().name("coursesUpdateForm"))
				.andExpect(model().attribute("entity", courseDTO));

		verify(courseService, times(1)).findByIdAsDTO(courseId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_whenCourseServiceThrowsServiceException_thenProcessError() throws Exception {
		Long courseId = 1L;

		String exceptionMessage = "Service Exception";
		when(courseService.findByIdAsDTO(courseId)).thenThrow(new ServiceException(exceptionMessage));

		mockMvc.perform(MockMvcRequestBuilders.get("/courses/update/{id}", courseId))
				.andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(courseService, times(1)).findByIdAsDTO(courseId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenValidCourse_thenRedirectSuccess() throws Exception {
		Long courseId = 1L;
		CourseDTO courseDTO = CourseDTO.builder().id(courseId).name("Course A").build();

		mockMvc.perform(post("/courses/update/{id}", courseId).with(csrf()).flashAttr("courseDTO", courseDTO))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/courses/update/" + courseId));

		verify(courseService, times(1)).save(courseDTO);
		verify(courseService, times(0)).findByIdAsDTO(any());
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenNotValidCourseByEmptyField_thenProcessForm() throws Exception {
		Long courseId = 1L;
		CourseDTO courseDTO = CourseDTO.builder().id(courseId).name("").build();
		when(courseService.findByIdAsDTO(courseId)).thenReturn(courseDTO);

		mockMvc.perform(post("/courses/update/{id}", courseId).with(csrf()).flashAttr("courseDTO", courseDTO))
				.andExpect(status().is2xxSuccessful()).andExpect(model().attributeExists("entity"))
				.andExpect(model().attribute("entity", courseDTO)).andExpect(view().name("coursesUpdateForm"));

		verify(courseService, times(0)).save(any(CourseDTO.class));
		verify(courseService, times(1)).findByIdAsDTO(courseId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenCourseServiceThrowValidationException_thenProcessForm() throws Exception {
		Long courseId = 1L;
		CourseDTO courseDTO = CourseDTO.builder().id(courseId).name("Course A").build();

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(courseService.save(courseDTO)).thenThrow(validationException);

		mockMvc.perform(post("/courses/update/{id}", courseId).with(csrf()).flashAttr("courseDTO", courseDTO))
				.andExpect(status().is3xxRedirection());

		verify(courseService, times(1)).save(courseDTO);
		verify(courseService, times(0)).findByIdAsDTO(any());
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenCourseServiceThrowServiceException_processError() throws Exception {
		Long courseId = 1L;
		CourseDTO courseDTO = CourseDTO.builder().id(courseId).name("Course A").build();
		String exceptionMessage = "Service Exception";

		when(courseService.save(courseDTO)).thenThrow(new ServiceException(exceptionMessage));

		mockMvc.perform(post("/courses/update/{id}", courseId).with(csrf()).flashAttr("courseDTO", courseDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(courseService, times(1)).save(courseDTO);
		verify(courseService, times(0)).findByIdAsDTO(any());
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void getInsertForm_happyPath() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/courses/insert")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("coursesInsertForm"));
	}


	@Test
	@WithMockUser(username = USERNAME)
	public void getInsertFrom_whenAccessDenied_processError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/courses/insert")).andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("exceptionMessage")).andExpect(view().name("error"));
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenValidCourse_thenRedirectSuccess() throws Exception {
		// Prepare a valid CourseDTO for insertion
		CourseDTO courseDTO = CourseDTO.builder().name("Course A").build();

		// Define the expected inserted course ID (assuming it's 1L)
		Long expectedInsertedId = 1L;

		// Mock the service method to return the expected ID
		when(courseService.save(courseDTO)).thenReturn(expectedInsertedId);

		mockMvc.perform(post("/courses/insert").with(csrf()).flashAttr("courseDTO", courseDTO))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/courses"))
				.andExpect(flash().attribute("insertedSuccessId", expectedInsertedId));

		verify(courseService, times(1)).save(courseDTO);
		verifyNoMoreInteractions(courseService);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenNotValidCourseByEmptyField_thenProcessForm() throws Exception {
		// Prepare a not valid CourseDTO with empty field
		CourseDTO courseDTO = CourseDTO.builder().name("") // Empty field
				.build();

		mockMvc.perform(post("/courses/insert").with(csrf()).flashAttr("courseDTO", courseDTO))
				.andExpect(status().is2xxSuccessful()).andExpect(view().name("coursesInsertForm"));

		verifyNoInteractions(courseService);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenCourseServiceThrowValidationException_thenProcessForm() throws Exception {
		// Prepare a valid CourseDTO
		CourseDTO courseDTO = CourseDTO.builder().name("Course A").build();

		// Define the validation exception message
		String validationErrorMessage = "Validation Error Message";

		// Mock the behavior of the courseService.save() method to throw a ValidationException
		when(courseService.save(courseDTO)).thenThrow(new ValidationException(validationErrorMessage));

		mockMvc.perform(post("/courses/insert").with(csrf()).flashAttr("courseDTO", courseDTO))
				.andExpect(status().is3xxRedirection());

		// Verify that the courseService.save() method was called once with the provided CourseDTO
		verify(courseService, times(1)).save(courseDTO);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenCourseServiceThrowServiceException_processError() throws Exception {
		// Prepare a valid CourseDTO
		CourseDTO courseDTO = CourseDTO.builder().name("Course A").build();

		// Define the service exception message
		String serviceExceptionMessage = "Service Exception Message";

		// Mock the behavior of the courseService.save() method to throw a ServiceException
		when(courseService.save(courseDTO)).thenThrow(new ServiceException(serviceExceptionMessage));

		mockMvc.perform(post("/courses/insert").with(csrf()).flashAttr("courseDTO", courseDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute("exceptionMessage", serviceExceptionMessage));

		// Verify that the courseService.save() method was called once with the provided CourseDTO
		verify(courseService, times(1)).save(courseDTO);
	}

}
