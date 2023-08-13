package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import com.university.schedule.service.ClassroomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassroomController.class)
public class ClassroomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassroomService classroomService;

    @Test
    public void getAll() throws Exception {
        List<Classroom> classrooms = new ArrayList<>();
        Building building = new Building("Building A", "Address A");
        classrooms.add(new Classroom(1L, "Room A", building));
        classrooms.add(new Classroom(2L, "Room B", building));

        when(classroomService.findAll(any())).thenReturn(classrooms);

        mockMvc.perform(get("/classrooms"))
                .andExpect(status().isOk())
                .andExpect(view().name("classrooms"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", classrooms));
    }

    @Test
    public void delete() throws Exception {
        Long classroomId = 1L;

        mockMvc.perform(get("/classrooms/delete/{id}", classroomId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classrooms")); // Check if redirected back to /classrooms

        verify(classroomService, times(1)).deleteById(classroomId);
    }

    @Test
    public void delete_whenClassroomServiceThrowsServiceException_thenRedirect() throws Exception {
        Long classroomId = 1L;

        doThrow(new ServiceException("Delete error")).when(classroomService).deleteById(classroomId);

        mockMvc.perform(get("/classrooms/delete/{id}", classroomId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classrooms")); // Check if redirected back to /classrooms

        verify(classroomService, times(1)).deleteById(classroomId);
    }
}
