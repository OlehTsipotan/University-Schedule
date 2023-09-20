package com.university.schedule.controller;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.DisciplineService;
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

@WebMvcTest(DisciplineRecordsController.class)
public class DisciplineRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DisciplineService disciplineService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_DISCIPLINES"})
	public void getAll_processPage() throws Exception {
		List<DisciplineDTO> disciplines = new ArrayList<>();
		disciplines.add(new DisciplineDTO(1L, "Discipline 1"));
		disciplines.add(new DisciplineDTO(2L, "Discipline 2"));

		when(disciplineService.findAllAsDTO(any(Pageable.class))).thenReturn(disciplines);

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
		DisciplineDTO disciplineDTO = new DisciplineDTO(disciplineId, "Discipline 1");

		when(disciplineService.findByIdAsDTO(disciplineId)).thenReturn(disciplineDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/disciplines/update/{id}", disciplineId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(view().name("disciplinesUpdateForm"))
				.andExpect(model().attribute("entity", disciplineDTO));

		verify(disciplineService, times(1)).findByIdAsDTO(disciplineId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_DISCIPLINES"})
	public void update_whenNotValidDiscipline_emptyField_thenProcessForm() throws Exception {
		Long disciplineId = 1L;

		DisciplineDTO disciplineDTO = new DisciplineDTO(disciplineId, ""); // Empty name

		when(disciplineService.findByIdAsDTO(disciplineId)).thenReturn(disciplineDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/disciplines/update/{id}", disciplineId).with(csrf())
						.flashAttr("disciplineDTO", disciplineDTO)).andExpect(status().is3xxRedirection())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", disciplineDTO))
				.andExpect(view().name("disciplinesUpdateForm"));

	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_DISCIPLINES"})
	public void update_whenDisciplineServiceThrowValidationException_thenProcessForm() throws Exception {
		Long disciplineId = 1L;

		DisciplineDTO disciplineDTO = new DisciplineDTO(disciplineId, "Discipline 1");

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(disciplineService.save((DisciplineDTO) any())).thenThrow(validationException);

		mockMvc.perform(MockMvcRequestBuilders.post("/disciplines/update/{id}", disciplineId).with(csrf())
				.flashAttr("disciplineDTO", disciplineDTO)).andExpect(status().is3xxRedirection());

		verify(disciplineService, times(1)).save(disciplineDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_DISCIPLINES"})
	public void update_whenDisciplineServiceThrowServiceException_thenProcessForm() throws Exception {
		Long disciplineId = 1L;

		DisciplineDTO disciplineDTO = new DisciplineDTO(disciplineId, "Discipline 1");

		ServiceException serviceException = new ServiceException("testException");

		when(disciplineService.save((DisciplineDTO) any())).thenThrow(serviceException);

		mockMvc.perform(MockMvcRequestBuilders.post("/disciplines/update/{id}", disciplineId).with(csrf())
				.flashAttr("disciplineDTO", disciplineDTO)).andExpect(status().is3xxRedirection());

		verify(disciplineService, times(1)).save(disciplineDTO);
	}
}

