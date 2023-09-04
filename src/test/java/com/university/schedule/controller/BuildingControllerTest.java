package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Building;
import com.university.schedule.service.BuildingService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(BuildingRecordsController.class)
public class BuildingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildingService buildingService;


    @Test
    public void getAll() throws Exception {
        List<Building> buildings = new ArrayList<>();
        buildings.add(new Building("Building A", "Address A"));
        buildings.add(new Building("Building B", "Address B"));

        when(buildingService.findAll((Pageable) any())).thenReturn((Page<Building>) buildings);

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings"))
                .andExpect(status().isOk())
                .andExpect(view().name("buildings"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", buildings));
    }

    @Test
    public void delete() throws Exception {
        Long buildingId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings/delete/{id}", buildingId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/buildings")); // Check if redirected back to /buildings

        verify(buildingService, times(1)).deleteById(buildingId);
    }

    @Test
    public void delete_whenBuildingServiceThrowsServiceException_thenErrorPage() throws Exception {
        Long buildingId = 1L;

        doThrow(new ServiceException("Delete error")).when(buildingService).deleteById(buildingId);

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings/delete/{id}", buildingId))
                .andExpect(model().attributeExists("message"));

        verify(buildingService, times(1)).deleteById(buildingId);
    }
}
