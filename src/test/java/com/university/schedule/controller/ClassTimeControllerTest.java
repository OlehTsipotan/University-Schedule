package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.ClassTime;
import com.university.schedule.service.ClassTimeService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(ClassTimeController.class)
public class ClassTimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassTimeService classTimeService;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Test
    public void getAll() throws Exception {
        List<ClassTime> classTimes = new ArrayList<>();
        classTimes.add(new ClassTime(1L, 1, LocalTime.of(8, 30), Duration.ofMinutes(95)));
        classTimes.add(new ClassTime(2L, 2, LocalTime.of(10, 20), Duration.ofMinutes(95)));

        when(classTimeService.findAll(any())).thenReturn(classTimes);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtimes"))
                .andExpect(status().isOk())
                .andExpect(view().name("classtimes"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", classTimes));
    }

    @Test
    public void delete() throws Exception {
        Long classTimeId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/classtimes/delete/{id}", classTimeId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classtimes")); // Check if redirected back to /classtimes

        verify(classTimeService, times(1)).deleteById(idCaptor.capture());
        assertEquals(classTimeId, idCaptor.getValue());
    }

    @Test
    public void delete_whenClassTimeServiceThrowsServiceException_thenRedirect() throws Exception {
        Long classTimeId = 1L;

        doThrow(new ServiceException("Delete error")).when(classTimeService).deleteById(classTimeId);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtimes/delete/{id}", classTimeId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classtimes")); // Check if redirected back to /classtimes

        verify(classTimeService, times(1)).deleteById(idCaptor.capture());
        assertEquals(classTimeId, idCaptor.getValue());
    }
}
