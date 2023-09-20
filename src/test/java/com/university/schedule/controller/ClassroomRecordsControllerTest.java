package com.university.schedule.controller;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.BuildingService;
import com.university.schedule.service.ClassroomService;
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

@WebMvcTest(ClassroomRecordsController.class)
public class ClassroomRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ClassroomService classroomService;

	@MockBean
	private BuildingService buildingService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_CLASSROOMS"})
	public void getAll_processPage() throws Exception {
		List<ClassroomDTO> classroomDTOs = new ArrayList<>();

		BuildingDTO buildingDTO = new BuildingDTO(1L, "buildingName", "buildingAddress");
		classroomDTOs.add(ClassroomDTO.builder().id(1L).name("Classroom A").buildingDTO(buildingDTO).build());
		classroomDTOs.add(ClassroomDTO.builder().id(2L).name("Classroom B").buildingDTO(buildingDTO).build());

		when(classroomService.findAllAsDTO((Pageable) any())).thenReturn(classroomDTOs);

		mockMvc.perform(get("/classrooms")).andExpect(status().isOk()).andExpect(view().name("classrooms"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", classroomDTOs));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
	public void delete() throws Exception {
		Long classroomId = 1L;

		mockMvc.perform(get("/classrooms/delete/{id}", classroomId)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/classrooms")); // Check if redirected back to /classrooms

		verify(classroomService, times(1)).deleteById(classroomId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
	public void delete_whenClassroomServiceThrowsDeletionFailedException_thenRedirectToErrorPage() throws Exception {
		Long classroomId = 1L;

		doThrow(new DeletionFailedException("Delete error")).when(classroomService).deleteById(classroomId);

		mockMvc.perform(MockMvcRequestBuilders.get("/classrooms/delete/{id}", classroomId))
				.andExpect(status().is3xxRedirection());

		verify(classroomService, times(1)).deleteById(classroomId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
	public void getUpdateForm() throws Exception {
		Long classroomId = 1L;
		BuildingDTO buildingDTO = new BuildingDTO(1L, "Building A", "Address A");
		ClassroomDTO classroomDTO =
				ClassroomDTO.builder().id(classroomId).name("Classroom A").buildingDTO(buildingDTO).build();

		List<BuildingDTO> buildingDTOList = List.of(buildingDTO);

		when(classroomService.findByIdAsDTO(classroomId)).thenReturn(classroomDTO);
		when(buildingService.findAllAsDTO()).thenReturn(buildingDTOList);

		mockMvc.perform(MockMvcRequestBuilders.get("/classrooms/update/{id}", classroomId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(view().name("classroomsUpdateForm"))
				.andExpect(model().attribute("buildingDTOList", buildingDTOList))
				.andExpect(model().attribute("entity", classroomDTO));

		verify(classroomService, times(1)).findByIdAsDTO(classroomId);
		verify(buildingService, times(1)).findAllAsDTO();
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
	public void update_whenNotValidClassroom_emptyField_thenProcessForm() throws Exception {

		Long classroomId = 1L;
		Long buildingId = 1L;

		BuildingDTO buildingDTO = BuildingDTO.builder().id(buildingId).name("Building A").address("Address A").build();

		List<BuildingDTO> buildingDTOList = List.of(buildingDTO);

		ClassroomDTO classroomDTO = ClassroomDTO.builder().name("").id(classroomId).buildingDTO(buildingDTO).build();

		when(classroomService.findByIdAsDTO(classroomId)).thenReturn(classroomDTO);
		when(buildingService.findAllAsDTO()).thenReturn(buildingDTOList);


		mockMvc.perform(MockMvcRequestBuilders.post("/classrooms/update/{id}", buildingId).with(csrf())
						.param("buildingDTO.id", String.valueOf(buildingId)).flashAttr("classroomDTO", classroomDTO))
				.andExpect(status().isOk()).andExpect(model().attributeExists("entity"))
				.andExpect(model().attribute("entity", classroomDTO))
				.andExpect(model().attribute("buildingDTOList", buildingDTOList))
				.andExpect(view().name("classroomsUpdateForm"));

		verify(classroomService, times(1)).findByIdAsDTO(classroomId);
		verify(buildingService, times(1)).findAllAsDTO();
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
	public void update_whenClassroomServiceThrowValidationException() throws Exception {

		Long classroomId = 1L;
		Long buildingId = 1L;

		BuildingDTO buildingDTO = BuildingDTO.builder().id(buildingId).name("Building A").address("Address A").build();

		List<BuildingDTO> buildingList = List.of(buildingDTO);

		ClassroomDTO classroomDTO =
				ClassroomDTO.builder().id(classroomId).name("Classroom A").buildingDTO(buildingDTO).build();

		when(classroomService.findByIdAsDTO(classroomId)).thenReturn(classroomDTO);
		when(buildingService.findAllAsDTO()).thenReturn(buildingList);

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(classroomService.save((ClassroomDTO) any())).thenThrow(validationException);

		mockMvc.perform(MockMvcRequestBuilders.post("/classrooms/update/{id}", classroomId).with(csrf())
						.param("buildingDTO.id", String.valueOf(buildingId)).flashAttr("classroomDTO", classroomDTO))
				.andExpect(status().is3xxRedirection());

		verify(classroomService, times(1)).save(classroomDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
	public void update_whenClassroomServiceThrowServiceException() throws Exception {

		Long classroomId = 1L;
		Long buildingId = 1L;

		BuildingDTO buildingDTO = BuildingDTO.builder().id(buildingId).name("Building A").address("Address A").build();

		List<BuildingDTO> buildingList = List.of(buildingDTO);

		ClassroomDTO classroomDTO =
				ClassroomDTO.builder().id(classroomId).name("Classroom A").buildingDTO(buildingDTO).build();

		when(classroomService.findByIdAsDTO(classroomId)).thenReturn(classroomDTO);
		when(buildingService.findAllAsDTO()).thenReturn(buildingList);

		ServiceException serviceException = new ServiceException("testException");

		when(classroomService.save((ClassroomDTO) any())).thenThrow(serviceException);


		mockMvc.perform(MockMvcRequestBuilders.post("/classrooms/update/{id}", classroomId).with(csrf())
						.param("buildingDTO.id", String.valueOf(buildingId)).flashAttr("classroomDTO", classroomDTO))
				.andExpect(status().is3xxRedirection());

		verify(classroomService, times(1)).save(classroomDTO);
	}
}
