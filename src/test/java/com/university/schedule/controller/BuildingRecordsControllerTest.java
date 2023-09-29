package com.university.schedule.controller;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Building;
import com.university.schedule.service.BuildingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
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

@WebMvcTest(BuildingRecordsController.class)
public class BuildingRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BuildingService buildingService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_BUILDINGS"})
	public void getAll_processPage() throws Exception {
		List<BuildingDTO> buildingList = new ArrayList<>();
		buildingList.add(new BuildingDTO(1L, "Building A", "Address A"));
		buildingList.add(new BuildingDTO(2L, "Building B", "Address B"));

		when(buildingService.findAllAsDTO((Pageable) any())).thenReturn(buildingList);

		mockMvc.perform(MockMvcRequestBuilders.get("/buildings")).andExpect(status().isOk())
				.andExpect(view().name("buildings"))
				.andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", buildingList));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_BUILDINGS"})
	public void delete() throws Exception {
		Long buildingId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/buildings/delete/{id}", buildingId))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/buildings")); // Check if redirected back to /buildings

		verify(buildingService, times(1)).deleteById(buildingId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_BUILDINGS"})
	public void delete_whenBuildingServiceThrowsDeletionFailedException_thenThrowDeletionFailedException()
			throws Exception {
		Long buildingId = 1L;

		doThrow(new DeletionFailedException("Delete error")).when(buildingService).deleteById(buildingId);

		mockMvc.perform(MockMvcRequestBuilders.get("/buildings/delete/{id}", buildingId)).andExpect(status().is3xxRedirection());

		verify(buildingService, times(1)).deleteById(buildingId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_BUILDINGS"})
	public void getUpdateForm() throws Exception {
		Long buildingId = 1L;
		BuildingDTO buildingDTO = new BuildingDTO(1L, "Building A", "Address A");

		when(buildingService.findByIdAsDTO(buildingId)).thenReturn(buildingDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/buildings/update/{id}", buildingId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(view().name("buildingsUpdateForm"))
				.andExpect(model().attribute("entity", buildingDTO));

		verify(buildingService, times(1)).findByIdAsDTO(buildingId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_BUILDINGS"})
	public void update_whenValidBuilding_thenRedirectSuccess() throws Exception {

		Long buildingId = 1L;

		Building building = Building.builder().id(buildingId).name("Building A").address("Address A").build();


		mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId).with(csrf())
						.param("id", building.getId().toString()).param("name", building.getName())
						.param("address", building.getAddress()).flashAttr("building", building))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/buildings/update/" + buildingId));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_BUILDINGS"})
	public void update_whenNotValidBuilding_emptyField_thenProcessForm() throws Exception {

		Long buildingId = 1L;

		BuildingDTO buildingDTO = BuildingDTO.builder().id(buildingId).name("").address("Address A").build();

		when(buildingService.findByIdAsDTO(buildingId)).thenReturn(buildingDTO);


		mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId).with(csrf())
						.param("id", buildingDTO.getId().toString()).param("name", buildingDTO.getName())
						.param("address", buildingDTO.getAddress()).flashAttr("building", buildingDTO))
				.andExpect(status().isOk()).andExpect(model().attributeExists("entity"))
				.andExpect(model().attribute("entity", buildingDTO)).andExpect(view().name("buildingsUpdateForm"));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_BUILDINGS"})
	public void update_whenBuildingServiceThrowValidationException() throws Exception {
		Long buildingId = 1L;

		BuildingDTO buildingDTO = BuildingDTO.builder().id(buildingId).name("Building A").address("Address A").build();

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(buildingService.save(buildingDTO)).thenThrow(validationException);

		mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId).with(csrf())
				.flashAttr("buildingDTO", buildingDTO)).andExpect(status().is3xxRedirection());

		verify(buildingService, times(1)).save(buildingDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_BUILDINGS"})
	public void update_whenBuildingServiceThrowServiceException() throws Exception {

		Long buildingId = 1L;

		BuildingDTO buildingDTO = BuildingDTO.builder().id(buildingId).name("Building A").address("Address A").build();

		ServiceException serviceException = new ServiceException("testException");

		when(buildingService.save(buildingDTO)).thenThrow(serviceException);

		mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId).with(csrf())
				.flashAttr("buildingDTO", buildingDTO)).andExpect(status().is3xxRedirection());
		verify(buildingService, times(1)).save(buildingDTO);
	}


}
