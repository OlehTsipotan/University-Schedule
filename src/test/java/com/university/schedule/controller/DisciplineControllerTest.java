package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Discipline;
import com.university.schedule.service.DisciplineService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DisciplineController.class)
public class DisciplineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DisciplineService disciplineService;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Test
    public void getAll() throws Exception {
        List<Discipline> disciplines = new ArrayList<>();
        disciplines.add(new Discipline(1L, "Math"));
        disciplines.add(new Discipline(2L, "History"));

        when(disciplineService.findAll(any())).thenReturn(disciplines);

        mockMvc.perform(MockMvcRequestBuilders.get("/disciplines"))
                .andExpect(status().isOk())
                .andExpect(view().name("disciplines"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", disciplines));
    }

    @Test
    public void delete() throws Exception {
        Long disciplineId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/disciplines/delete/{id}", disciplineId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/disciplines")); // Check if redirected back to /disciplines

        verify(disciplineService, times(1)).deleteById(idCaptor.capture());
        assertEquals(disciplineId, idCaptor.getValue());
    }

    @Test
    public void delete_whenDisciplineServiceThrowsServiceException_thenRedirect() throws Exception {
        Long disciplineId = 1L;

        doThrow(new ServiceException("Delete error")).when(disciplineService).deleteById(disciplineId);

        mockMvc.perform(MockMvcRequestBuilders.get("/disciplines/delete/{id}", disciplineId))
                .andExpect(model().attributeExists("message"));

        verify(disciplineService, times(1)).deleteById(idCaptor.capture());
        assertEquals(disciplineId, idCaptor.getValue());
    }
}
