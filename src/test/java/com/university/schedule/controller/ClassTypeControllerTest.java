package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.ClassType;
import com.university.schedule.service.ClassTypeService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(ClassTypeController.class)
public class ClassTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassTypeService classTypeService;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Test
    public void getAll() throws Exception {
        List<ClassType> classTypes = new ArrayList<>();
        classTypes.add(new ClassType(1L, "Lecture"));
        classTypes.add(new ClassType(2L, "Lab"));

        when(classTypeService.findAll((Sort) any())).thenReturn(classTypes);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtypes"))
                .andExpect(status().isOk())
                .andExpect(view().name("classtypes"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", classTypes));
    }

    @Test
    public void delete() throws Exception {
        Long classTypeId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/classtypes/delete/{id}", classTypeId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classtypes")); // Check if redirected back to /classtypes

        verify(classTypeService, times(1)).deleteById(idCaptor.capture());
        assertEquals(classTypeId, idCaptor.getValue());
    }

    @Test
    public void delete_whenClassTypeServiceThrowsServiceException_thenRedirect() throws Exception {
        Long classTypeId = 1L;

        doThrow(new ServiceException("Delete error")).when(classTypeService).deleteById(classTypeId);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtypes/delete/{id}", classTypeId))
                .andExpect(model().attributeExists("message"));

        verify(classTypeService, times(1)).deleteById(idCaptor.capture());
        assertEquals(classTypeId, idCaptor.getValue());
    }
}
