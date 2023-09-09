package com.university.schedule.controller;

import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import com.university.schedule.service.BuildingService;
import com.university.schedule.service.ClassroomDTOService;
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

    @MockBean
    private ClassroomDTOService classroomDTOService;

    @Test
    @WithMockUser(username = "username", authorities = {"VIEW_CLASSROOMS"})
    public void getAll_processPage() throws Exception {
        List<ClassroomDTO> classroomDTOs = new ArrayList<>();
        String buildingName = "Building A";
        classroomDTOs.add(new ClassroomDTO(1L, "Room A", buildingName));
        classroomDTOs.add(new ClassroomDTO(2L, "Room B", buildingName));

        when(classroomDTOService.findAll((Pageable) any())).thenReturn(classroomDTOs);

        mockMvc.perform(get("/classrooms"))
                .andExpect(status().isOk())
                .andExpect(view().name("classrooms"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", classroomDTOs));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
    public void delete() throws Exception {
        Long classroomId = 1L;

        mockMvc.perform(get("/classrooms/delete/{id}", classroomId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classrooms")); // Check if redirected back to /classrooms

        verify(classroomService, times(1)).deleteById(classroomId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
    public void delete_whenBuildingServiceThrowsServiceException_thenRedirectToErrorPage() throws Exception {
        Long classroomId = 1L;

        doThrow(new ServiceException("Delete error")).when(classroomService).deleteById(classroomId);

        mockMvc.perform(MockMvcRequestBuilders.get("/classrooms/delete/{id}", classroomId))
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("message"));


        verify(classroomService, times(1)).deleteById(classroomId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
    public void getUpdateForm() throws Exception {
        Long classroomId = 1L;
        Building building = new Building("Building A", "Address A");
        Classroom classroom = new Classroom("Classroom A", building);

        List<Building> buildingList = List.of(building);

        when(classroomService.findById(classroomId)).thenReturn(classroom);
        when(buildingService.findAll()).thenReturn(buildingList);

        mockMvc.perform(MockMvcRequestBuilders.get("/classrooms/update/{id}", classroomId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(view().name("classroomsUpdateForm"))
                .andExpect(model().attribute("buildings", buildingList))
                .andExpect(model().attribute("entity", classroom));

        verify(classroomService, times(1)).findById(classroomId);
        verify(buildingService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
    public void update_whenNotValidClassroom_emptyField_thenProcessForm() throws Exception {

        Long classroomId = 1L;
        Long buildingId = 1L;

        Building building = Building.builder()
                .id(buildingId)
                .name("Building A")
                .address("Address A")
                .build();

        List<Building> buildingList = List.of(building);

        Classroom classroom = Classroom.builder()
                .name("")
                .id(classroomId)
                .building(building)
                .build();

        when(classroomService.findById(classroomId)).thenReturn(classroom);
        when(buildingService.findAll()).thenReturn(buildingList);


        mockMvc.perform(MockMvcRequestBuilders.post("/classrooms/update/{id}", buildingId)
                        .with(csrf())
                        .flashAttr("classroom", classroom))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", classroom))
                .andExpect(model().attribute("buildings", buildingList))
                .andExpect(view().name("classroomsUpdateForm"));

        verify(classroomService, times(1)).findById(classroomId);
        verify(buildingService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
    public void update_whenClassroomServiceThrowValidationException_thenProcessForm() throws Exception {

        Long classroomId = 1L;
        Long buildingId = 1L;

        Building building = Building.builder()
                .id(buildingId)
                .name("Building A")
                .address("Address A")
                .build();

        List<Building> buildingList = List.of(building);

        Classroom classroom = Classroom.builder()
                .id(classroomId)
                .name("Classroom A")
                .building(building)
                .build();

        when(classroomService.findById(classroomId)).thenReturn(classroom);
        when(buildingService.findAll()).thenReturn(buildingList);

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(classroomService.save(any())).thenThrow(validationException);

        mockMvc.perform(MockMvcRequestBuilders.post("/classrooms/update/{id}", classroomId)
                        .with(csrf())
                        .flashAttr("classroom", classroom))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("validationServiceErrors"))
                .andExpect(model().attribute("validationServiceErrors", validationException.getViolations()))
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", classroom))
                .andExpect(model().attribute("buildings", buildingList))
                .andExpect(view().name("classroomsUpdateForm"));

        verify(classroomService, times(1)).save(classroom);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSROOMS"})
    public void update_whenClassroomServiceThrowServiceException_thenProcessForm() throws Exception {

        Long classroomId = 1L;
        Long buildingId = 1L;

        Building building = Building.builder()
                .id(buildingId)
                .name("Building A")
                .address("Address A")
                .build();

        List<Building> buildingList = List.of(building);

        Classroom classroom = Classroom.builder()
                .id(classroomId)
                .name("Classroom A")
                .building(building)
                .build();

        when(classroomService.findById(classroomId)).thenReturn(classroom);
        when(buildingService.findAll()).thenReturn(buildingList);

        ServiceException serviceException = new ServiceException("testException");

        when(classroomService.save(any())).thenThrow(serviceException);


        mockMvc.perform(MockMvcRequestBuilders.post("/classrooms/update/{id}", buildingId)
                        .with(csrf())
                        .flashAttr("classroom", classroom))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("serviceError"))
                .andExpect(model().attribute("serviceError", serviceException.getMessage()))
                .andExpect(model().attribute("entity", classroom))
                .andExpect(model().attribute("buildings", buildingList))
                .andExpect(view().name("classroomsUpdateForm"));
    }
}
