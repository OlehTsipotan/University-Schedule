package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Discipline;
import com.university.schedule.service.DisciplineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
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

@WebMvcTest(DisciplineRecordsController.class)
public class DisciplineRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DisciplineService disciplineService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_DISCIPLINES"})
	public void getAll_processPage() throws Exception {
		List<Discipline> disciplines = new ArrayList<>();
		disciplines.add(new Discipline(1L, "Discipline 1"));
		disciplines.add(new Discipline(2L, "Discipline 2"));

		when(disciplineService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(disciplines));

		mockMvc.perform(MockMvcRequestBuilders.get(("/disciplines"))).andExpect(status().isOk())
				.andExpect(view().name("disciplines"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", disciplines));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_DISCIPLINES"})
	public void delete() throws Exception {
		Long disciplineId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/disciplines/delete/{id}", disciplineId))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/disciplines"));

		verify(disciplineService, times(1)).deleteById(disciplineId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_DISCIPLINES"})
	public void getUpdateForm() throws Exception {
		Long disciplineId = 1L;
		Discipline discipline = new Discipline(disciplineId, "Discipline 1");

		when(disciplineService.findById(disciplineId)).thenReturn(discipline);

		mockMvc.perform(MockMvcRequestBuilders.get("/disciplines/update/{id}", disciplineId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(view().name("disciplinesUpdateForm"))
				.andExpect(model().attribute("entity", discipline));

		verify(disciplineService, times(1)).findById(disciplineId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_DISCIPLINES"})
	public void update_whenNotValidDiscipline_emptyField_thenProcessForm() throws Exception {
		Long disciplineId = 1L;

		Discipline discipline = new Discipline(disciplineId, ""); // Empty name

		when(disciplineService.findById(disciplineId)).thenReturn(discipline);

		mockMvc.perform(MockMvcRequestBuilders.post("/disciplines/update/{id}", disciplineId).with(csrf())
						.flashAttr("discipline", discipline)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", discipline))
				.andExpect(view().name("disciplinesUpdateForm"));

	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_DISCIPLINES"})
	public void update_whenDisciplineServiceThrowValidationException_thenProcessForm() throws Exception {
		Long disciplineId = 1L;

		Discipline discipline = new Discipline(disciplineId, "Discipline 1");

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(disciplineService.save(any())).thenThrow(validationException);
		when(disciplineService.findById(disciplineId)).thenReturn(discipline);

		mockMvc.perform(MockMvcRequestBuilders.post("/disciplines/update/{id}", disciplineId).with(csrf())
						.flashAttr("discipline", discipline)).andExpect(status().isOk())
				.andExpect(model().attributeExists("validationServiceErrors"))
				.andExpect(model().attribute("validationServiceErrors", validationException.getViolations()))
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", discipline))
				.andExpect(view().name("disciplinesUpdateForm"));

		verify(disciplineService, times(1)).save(discipline);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_DISCIPLINES"})
	public void update_whenDisciplineServiceThrowServiceException_thenProcessForm() throws Exception {
		Long disciplineId = 1L;

		Discipline discipline = new Discipline(disciplineId, "Discipline 1");

		ServiceException serviceException = new ServiceException("testException");

		when(disciplineService.save(any())).thenThrow(serviceException);
		when(disciplineService.findById(disciplineId)).thenReturn(discipline);

		mockMvc.perform(MockMvcRequestBuilders.post("/disciplines/update/{id}", disciplineId).with(csrf())
						.flashAttr("discipline", discipline)).andExpect(status().isOk())
				.andExpect(model().attributeExists("serviceError"))
				.andExpect(model().attribute("serviceError", serviceException.getMessage()))
				.andExpect(model().attribute("entity", discipline)).andExpect(view().name("disciplinesUpdateForm"));
	}
}

