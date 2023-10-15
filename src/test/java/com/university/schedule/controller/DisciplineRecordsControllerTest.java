package com.university.schedule.controller;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.CourseService;
import com.university.schedule.service.DisciplineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DisciplineRecordsController.class)
@ActiveProfiles("test")
public class DisciplineRecordsControllerTest {

	private static final String USERNAME = "testUsername";
	private static final String VIEW_AUTHORITY = "VIEW_DISCIPLINES";
	private static final String EDIT_AUTHORITY = "EDIT_DISCIPLINES";
	private static final String INSERT_AUTHORITY = "INSERT_DISCIPLINES";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DisciplineService disciplineService;

	@Test
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenNoArgs_happyPath() throws Exception {
		List<DisciplineDTO> disciplineList = new ArrayList<>();
		disciplineList.add(new DisciplineDTO(1L, "Discipline A"));
		disciplineList.add(new DisciplineDTO(2L, "Discipline B"));

		when(disciplineService.findAllAsDTO(any())).thenReturn(disciplineList);

		mockMvc.perform(MockMvcRequestBuilders.get("/disciplines?offset=5"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("disciplines"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", disciplineList));

		verify(disciplineService, times(1)).findAllAsDTO(any());
	}


	@ParameterizedTest
	@CsvSource({"5, 10"})
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenLimitAndOffsetArgs_happyPath(int limit, int offset) throws Exception {
		List<DisciplineDTO> disciplineList = new ArrayList<>();
		disciplineList.add(new DisciplineDTO(1L, "Discipline A"));
		disciplineList.add(new DisciplineDTO(2L, "Discipline B"));

		when(disciplineService.findAllAsDTO(any())).thenReturn(disciplineList);

		mockMvc.perform(MockMvcRequestBuilders.get(String.format("/disciplines?offset=%d&limit=%d", offset, limit)))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("disciplines"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", disciplineList));

		verify(disciplineService, times(1)).findAllAsDTO(
				argThat(pageable -> pageable.getOffset() == offset && pageable.getPageSize() == limit));
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_happyPath() throws Exception {
		// Test code for delete method
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_whenDisciplineServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
		Long disciplineId = 1L;
		String exceptionMessage = "Deletion Failed";

		doThrow(new DeletionFailedException(exceptionMessage)).when(disciplineService).deleteById(disciplineId);

		mockMvc.perform(MockMvcRequestBuilders.get("/disciplines/delete/{id}", disciplineId))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("error"))
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(disciplineService, times(1)).deleteById(disciplineId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_happyPath() throws Exception {
		Long disciplineId = 1L;
		DisciplineDTO disciplineDTO = new DisciplineDTO(1L, "Math");

		when(disciplineService.findByIdAsDTO(disciplineId)).thenReturn(disciplineDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/disciplines/update/{id}", disciplineId))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("entity"))
				.andExpect(view().name("disciplinesUpdateForm"))
				.andExpect(model().attribute("entity", disciplineDTO));

		verify(disciplineService, times(1)).findByIdAsDTO(disciplineId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_whenDisciplineServiceThrowsServiceException_thenProcessError() throws Exception {
		Long disciplineId = 1L;
		String exceptionMessage = "Service Exception";

		when(disciplineService.findByIdAsDTO(disciplineId)).thenThrow(new ServiceException(exceptionMessage));

		mockMvc.perform(MockMvcRequestBuilders.get("/disciplines/update/{id}", disciplineId))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("error"))
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(disciplineService, times(1)).findByIdAsDTO(disciplineId);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenValidDisciplineDTO_thenRedirectSuccess() throws Exception {
		Long disciplineId = 1L;
		DisciplineDTO disciplineDTO = DisciplineDTO.builder().id(disciplineId).name("Valid Name").build();

		mockMvc.perform(post("/disciplines/update/{id}", disciplineId).with(csrf())
						.flashAttr("disciplineDTO", disciplineDTO))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/disciplines/update/" + disciplineId));

		verify(disciplineService, times(1)).save(disciplineDTO);
		verify(disciplineService, times(0)).findByIdAsDTO(any());
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenNotValidDisciplineDTOByEmptyField_thenProcessForm() throws Exception {
		Long disciplineId = 1L;
		DisciplineDTO disciplineDTO = DisciplineDTO.builder().id(disciplineId).name("").build();

		when(disciplineService.findByIdAsDTO(disciplineId)).thenReturn(disciplineDTO);

		mockMvc.perform(post("/disciplines/update/{id}", disciplineId).with(csrf())
						.flashAttr("disciplineDTO", disciplineDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("entity"))
				.andExpect(model().attribute("entity", disciplineDTO))
				.andExpect(view().name("disciplinesUpdateForm"));

		verify(disciplineService, times(0)).save(any(DisciplineDTO.class));
		verify(disciplineService, times(1)).findByIdAsDTO(disciplineId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenDisciplineServiceThrowValidationException_thenProcessForm() throws Exception {
		Long disciplineId = 1L;
		DisciplineDTO disciplineDTO = DisciplineDTO.builder().id(disciplineId).name("Some Name").build();

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(disciplineService.save(disciplineDTO)).thenThrow(validationException);

		mockMvc.perform(post("/disciplines/update/{id}", disciplineId).with(csrf())
						.flashAttr("disciplineDTO", disciplineDTO))
				.andExpect(status().is3xxRedirection());

		verify(disciplineService, times(1)).save(disciplineDTO);
		verify(disciplineService, times(0)).findByIdAsDTO(anyLong());
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenCDisciplineServiceThrowServiceException_processError() throws Exception {
		Long disciplineId = 1L;
		DisciplineDTO disciplineDTO = DisciplineDTO.builder().id(disciplineId).name("Some Name").build();
		String exceptionMessage = "Service Exception";

		when(disciplineService.save(disciplineDTO)).thenThrow(new ServiceException(exceptionMessage));

		mockMvc.perform(post("/disciplines/update/{id}", disciplineId).with(csrf())
						.flashAttr("disciplineDTO", disciplineDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(disciplineService, times(1)).save(disciplineDTO);
		verify(disciplineService, times(0)).findByIdAsDTO(anyLong());
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void getInsertForm_happyPath() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/disciplines/insert"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("disciplinesInsertForm"));
	}


	@Test
	@WithMockUser(username = USERNAME)
	public void getInsertFrom_whenAccessDenied_processError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/disciplines/insert"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("exceptionMessage"))
				.andExpect(view().name("error"));
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenValidDisciplineDTO_thenRedirectSuccess() throws Exception {
		DisciplineDTO disciplineDTO = DisciplineDTO.builder()
				.name("Math")
				.build();

		mockMvc.perform(post("/disciplines/insert").with(csrf())
						.flashAttr("disciplineDTO", disciplineDTO))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/disciplines"))
				.andExpect(flash().attribute("insertedSuccessId", 0L));

		verify(disciplineService, times(1)).save(disciplineDTO);
		verifyNoMoreInteractions(disciplineService);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenNotValidDisciplineDTOByEmptyField_thenProcessForm() throws Exception {
		DisciplineDTO disciplineDTO = DisciplineDTO.builder()
				.name("")  // Empty field
				.build();

		mockMvc.perform(post("/disciplines/insert").with(csrf())
						.flashAttr("disciplineDTO", disciplineDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("disciplinesInsertForm"));

		verifyNoInteractions(disciplineService);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenDisciplineServiceThrowValidationException_thenProcessForm() throws Exception {
		DisciplineDTO disciplineDTO = DisciplineDTO.builder()
				.name("Discipline A")
				.build();

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(disciplineService.save(disciplineDTO)).thenThrow(validationException);

		mockMvc.perform(post("/disciplines/insert").with(csrf())
						.flashAttr("disciplineDTO", disciplineDTO))
				.andExpect(status().is3xxRedirection());

		verify(disciplineService, times(1)).save(disciplineDTO);
		verifyNoMoreInteractions(disciplineService);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenDisciplineServiceThrowServiceException_processError() throws Exception {
		DisciplineDTO disciplineDTO = DisciplineDTO.builder()
				.name("Discipline A")
				.build();

		String exceptionMessage = "Service Exception";

		when(disciplineService.save(disciplineDTO)).thenThrow(new ServiceException(exceptionMessage));

		mockMvc.perform(post("/disciplines/insert").with(csrf())
						.flashAttr("disciplineDTO", disciplineDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(disciplineService, times(1)).save(disciplineDTO);
		verifyNoMoreInteractions(disciplineService);
	}

}
