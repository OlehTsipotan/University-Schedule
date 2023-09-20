package com.university.schedule.controller;


import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.ClassTimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassTimeRecordsController.class)
public class ClassTimeRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ClassTimeService classTimeService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_CLASSTIMES"})
	public void getAll_processPage() throws Exception {
		List<ClassTimeDTO> classTimeDTOs = new ArrayList<>();
		classTimeDTOs.add(new ClassTimeDTO(1L, 1, LocalTime.of(9, 0), 95));
		classTimeDTOs.add(new ClassTimeDTO(2L, 2, LocalTime.of(11, 0), 95));

		when(classTimeService.findAllAsDTO(any(Pageable.class))).thenReturn(classTimeDTOs);

		mockMvc.perform(get("/classtimes")).andExpect(status().isOk()).andExpect(view().name("classtimes"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", classTimeDTOs));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSTIMES"})
	public void delete() throws Exception {
		Long classTimeId = 1L;

		mockMvc.perform(get("/classtimes/delete/{id}", classTimeId)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/classtimes"));

		verify(classTimeService, times(1)).deleteById(classTimeId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
	public void delete_whenClassTimeServiceThrowsDeletionFailedException_thenRedirectToErrorPage() throws Exception {
		Long classroomId = 1L;

		doThrow(new DeletionFailedException("Delete error")).when(classTimeService).deleteById(classroomId);

		mockMvc.perform(MockMvcRequestBuilders.get("/classtimes/delete/{id}", classroomId))
				.andExpect(status().is3xxRedirection());

		verify(classTimeService, times(1)).deleteById(classroomId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSTIMES"})
	public void getUpdateForm() throws Exception {
		Long classTimeId = 1L;
		ClassTimeDTO classTimeDTO = new ClassTimeDTO(classTimeId, 1, LocalTime.of(9, 0), 95);

		when(classTimeService.findByIdAsDTO(classTimeId)).thenReturn(classTimeDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/classtimes/update/{id}", classTimeId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(view().name("classtimesUpdateForm"))
				.andExpect(model().attribute("entity", classTimeDTO));

		verify(classTimeService, times(1)).findByIdAsDTO(classTimeId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSTIMES"})
	public void update_whenNotValidClassTime_emptyField_thenProcessForm() throws Exception {
		Long classTimeId = 1L;

		ClassTimeDTO classTimeDTO = new ClassTimeDTO(classTimeId, 1, null, 95);

		when(classTimeService.findByIdAsDTO(classTimeId)).thenReturn(classTimeDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/classtimes/update/{id}", classTimeId).with(csrf())
						.flashAttr("classTimeDTO", classTimeDTO)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", classTimeDTO))
				.andExpect(view().name("classtimesUpdateForm"));

		verify(classTimeService, times(0)).save((ClassTimeDTO) any());
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSTIMES"})
	public void update_whenClassTimeServiceThrowValidationException() throws Exception {
		Long classTimeId = 1L;

		ClassTimeDTO classTimeDTO = new ClassTimeDTO(classTimeId, 1, LocalTime.of(9, 0), 95);

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(classTimeService.save((ClassTimeDTO) any())).thenThrow(validationException);
		when(classTimeService.findByIdAsDTO(classTimeId)).thenReturn(classTimeDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/classtimes/update/{id}", classTimeId).with(csrf())
				.flashAttr("classTimeDTO", classTimeDTO)).andExpect(status().is3xxRedirection());

		verify(classTimeService, times(1)).save(classTimeDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSTIMES"})
	public void update_whenClassTimeServiceThrowServiceException() throws Exception {
		Long classTimeId = 1L;

		ClassTimeDTO classTimeDTO = new ClassTimeDTO(classTimeId, 1, LocalTime.of(9, 0), 95);

		ServiceException serviceException = new ServiceException("testException");

		when(classTimeService.save((ClassTimeDTO) any())).thenThrow(serviceException);
		when(classTimeService.findByIdAsDTO(classTimeId)).thenReturn(classTimeDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/classtimes/update/{id}", classTimeId).with(csrf())
				.flashAttr("classTimeDTO", classTimeDTO)).andExpect(status().is3xxRedirection());

		verify(classTimeService, times(1)).save(classTimeDTO);
	}
}

