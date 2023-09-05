package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Building;
import com.university.schedule.service.BuildingService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(BuildingRecordsController.class)
public class BuildingRecordsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildingService buildingService;


    @Test
    @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "VIEW_BUILDINGS"})
    public void getAll_whenAllIsOk_processPage() throws Exception {
        List<Building> buildingList = new ArrayList<>();
        buildingList.add(new Building("Building A", "Address A"));
        buildingList.add(new Building("Building B", "Address B"));
        Page<Building> buildingPage = new PageImpl<>(buildingList);

        when(buildingService.findAll((Pageable) any())).thenReturn(buildingPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings"))
                .andExpect(status().isOk())
                .andExpect(view().name("buildings"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", buildingList));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "EDIT_BUILDINGS"})
    public void delete() throws Exception {
        Long buildingId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings/delete/{id}", buildingId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/buildings")); // Check if redirected back to /buildings

        verify(buildingService, times(1)).deleteById(buildingId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "EDIT_BUILDINGS"})
    public void delete_whenBuildingServiceThrowsServiceException_thenErrorPage() throws Exception {
        Long buildingId = 1L;

        doThrow(new ServiceException("Delete error")).when(buildingService).deleteById(buildingId);

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings/delete/{id}", buildingId))
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("message"));


        verify(buildingService, times(1)).deleteById(buildingId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "EDIT_BUILDINGS"})
    public void getUpdateForm() throws Exception {
        Long buildingId = 1L;
        Building building = new Building("Building A", "Address A");

        when(buildingService.findById(buildingId)).thenReturn(building);

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings/update/{id}", buildingId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(view().name("buildingsUpdateForm"))
                .andExpect(model().attribute("entity", building));

        verify(buildingService, times(1)).findById(buildingId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "EDIT_BUILDINGS"})
    public void update_whenValidBuilding_thenRedirectSuccess() throws Exception {

        Long buildingId = 1L;

        Building building = Building.builder()
                .id(buildingId)
                .name("Building A")
                .address("Address A")
                .build();


        mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId)
                        .with(csrf())
                        .param("id", building.getId().toString())
                        .param("name", building.getName())
                        .param("address", building.getAddress())
                        .flashAttr("building", building))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/buildings/update/1?success"));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "EDIT_BUILDINGS"})
    public void update_whenNotValidBuilding_emptyField_thenProcessForm() throws Exception {

        Long buildingId = 1L;

        Building building = Building.builder()
                .id(buildingId)
                .name("")
                .address("Address A")
                .build();

        when(buildingService.findById(buildingId)).thenReturn(building);


        mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId)
                        .with(csrf())
                        .param("id", building.getId().toString())
                        .param("name", building.getName())
                        .param("address", building.getAddress())
                        .flashAttr("building", building))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", building))
                .andExpect(view().name("buildingsUpdateForm"));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "EDIT_BUILDINGS"})
    public void update_whenBuildingServiceThrowValidationException_thenProcessForm() throws Exception {

        Long buildingId = 1L;

        Building building = Building.builder()
                .id(buildingId)
                .name("Building A")
                .address("Address A")
                .build();

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(buildingService.findById(buildingId)).thenReturn(building);
        when(buildingService.save(building)).thenThrow(validationException);


        mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId)
                        .with(csrf())
                        .param("id", building.getId().toString())
                        .param("name", building.getName())
                        .param("address", building.getAddress())
                        .flashAttr("building", building))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attributeExists("validationServiceErrors"))
                .andExpect(model().attribute("validationServiceErrors", validationException.getViolations()))
                .andExpect(model().attribute("entity", building))
                .andExpect(view().name("buildingsUpdateForm"));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"ROLE_ADMIN", "EDIT_BUILDINGS"})
    public void update_whenBuildingServiceThrowServiceException_thenProcessForm() throws Exception {

        Long buildingId = 1L;

        Building building = Building.builder()
                .id(buildingId)
                .name("Building A")
                .address("Address A")
                .build();

        ServiceException serviceException = new ServiceException("testException");

        when(buildingService.findById(buildingId)).thenReturn(building);
        when(buildingService.save(building)).thenThrow(serviceException);


        mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId)
                        .with(csrf())
                        .param("id", building.getId().toString())
                        .param("name", building.getName())
                        .param("address", building.getAddress())
                        .flashAttr("building", building))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attributeExists("serviceError"))
                .andExpect(model().attribute("serviceError", serviceException.getMessage()))
                .andExpect(model().attribute("entity", building))
                .andExpect(view().name("buildingsUpdateForm"));
    }



}
