package com.university.schedule.controller;

import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.ClassTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
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

@WebMvcTest(ClassTypeRecordsController.class)
@ComponentScan("com.university.schedule.formatter")
public class ClassTypeRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ClassTypeService classTypeService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_CLASSTYPES"})
	public void getAll_processPage() throws Exception {
		List<ClassTypeDTO> classTypes = new ArrayList<>();
		classTypes.add(new ClassTypeDTO(1L, "Type 1"));
		classTypes.add(new ClassTypeDTO(2L, "Type 2"));

		when(classTypeService.findAllAsDTO(any(Pageable.class))).thenReturn(classTypes);

		mockMvc.perform(get("/classtypes")).andExpect(status().isOk()).andExpect(view().name("classtypes"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", classTypes));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSTYPES"})
	public void delete() throws Exception {
		Long classTypeId = 1L;

		mockMvc.perform(get("/classtypes/delete/{id}", classTypeId)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/classtypes"));

		verify(classTypeService, times(1)).deleteById(classTypeId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSTYPES"})
	public void getUpdateForm() throws Exception {
		Long classTypeId = 1L;
		ClassTypeDTO classTypeDTO = new ClassTypeDTO(classTypeId, "Type 1");

		when(classTypeService.findByIdAsDTO(classTypeId)).thenReturn(classTypeDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/classtypes/update/{id}", classTypeId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(view().name("classtypesUpdateForm"))
				.andExpect(model().attribute("entity", classTypeDTO));

		verify(classTypeService, times(1)).findByIdAsDTO(classTypeId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSTYPES"})
	public void update_whenNotValidClassType_emptyField_thenProcessForm() throws Exception {
		Long classTypeId = 1L;

		ClassTypeDTO classTypeDTO = new ClassTypeDTO(classTypeId, ""); // Empty name

		when(classTypeService.findByIdAsDTO(classTypeId)).thenReturn(classTypeDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/classtypes/update/{id}", classTypeId).with(csrf())
						.flashAttr("classType", classTypeDTO)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", classTypeDTO))
				.andExpect(view().name("classtypesUpdateForm"));

		verify(classTypeService, times(0)).save((ClassTypeDTO) any());
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSTYPES"})
	public void update_whenClassTypeServiceThrowValidationException_thenProcessForm() throws Exception {
		Long classTypeId = 1L;

		ClassTypeDTO classTypeDTO = new ClassTypeDTO(classTypeId, "Type 1");

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(classTypeService.save((ClassTypeDTO) any())).thenThrow(validationException);
		when(classTypeService.findByIdAsDTO(classTypeId)).thenReturn(classTypeDTO);


		mockMvc.perform(MockMvcRequestBuilders.post("/classtypes/update/{id}", classTypeId).with(csrf())
				.flashAttr("classTypeDTO", classTypeDTO)).andExpect(status().is3xxRedirection());

		verify(classTypeService, times(1)).save(classTypeDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSTYPES"})
	public void update_whenClassTypeServiceThrowServiceException_thenProcessForm() throws Exception {
		Long classTypeId = 1L;

		ClassTypeDTO classTypeDTO = new ClassTypeDTO(classTypeId, "Type 1");

		ServiceException serviceException = new ServiceException("testException");

		when(classTypeService.save((ClassTypeDTO) any())).thenThrow(serviceException);
		when(classTypeService.findByIdAsDTO(classTypeId)).thenReturn(classTypeDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/classtypes/update/{id}", classTypeId).with(csrf())
				.flashAttr("classTypeDTO", classTypeDTO)).andExpect(status().is3xxRedirection());
		verify(classTypeService, times(1)).save(classTypeDTO);

	}
}