package com.university.schedule.controller;

import com.university.schedule.controller.TeacherController;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Teacher;
import com.university.schedule.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherController.class)
public class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @Test
    public void testGetAll() throws Exception {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher(1L, "teacher1@example.com", "password", "John", "Doe"));
        teachers.add(new Teacher(2L, "teacher2@example.com", "password", "Jane", "Smith"));

        when(teacherService.findAll(any())).thenReturn(teachers);

        mockMvc.perform(MockMvcRequestBuilders.get("/teachers"))
                .andExpect(status().isOk())
                .andExpect(view().name("teachers"))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(model().attribute("teachers", teachers));
    }

    @Test
    public void testDelete() throws Exception {
        Long teacherId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/teachers/delete/{id}", teacherId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers")); // Check if redirected back to /teachers

        verify(teacherService, times(1)).deleteById(teacherId);
    }

    @Test
    public void testDelete_whenTeacherServiceThrowsServiceException_thenRedirect() throws Exception {
        Long teacherId = 1L;

        doThrow(new ServiceException("Delete error")).when(teacherService).deleteById(teacherId);

        mockMvc.perform(MockMvcRequestBuilders.get("/teachers/delete/{id}", teacherId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers")); // Check if redirected back to /teachers

        verify(teacherService, times(1)).deleteById(teacherId);
    }
}
