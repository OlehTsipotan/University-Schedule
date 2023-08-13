package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Test
    public void getAll() throws Exception {
        List<Student> students = new ArrayList<>();
        students.add(new Student(1L, "teacher1@example.com", "password", "John", "Doe"));
        students.add(new Student(1L, "teacher2@example.com", "password", "Mary", "Brown"));

        when(studentService.findAll(any())).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students"))
                .andExpect(model().attributeExists("students", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("students", students));
    }

    @Test
    public void delete() throws Exception {
        Long studentId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/students/delete/{id}", studentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students")); // Check if redirected back to /students

        verify(studentService, times(1)).deleteById(idCaptor.capture());
        assertEquals(studentId, idCaptor.getValue());
    }

    @Test
    public void delete_whenStudentServiceThrowsServiceException_thenRedirect() throws Exception {
        Long studentId = 1L;

        doThrow(new ServiceException("Delete error")).when(studentService).deleteById(studentId);

        mockMvc.perform(MockMvcRequestBuilders.get("/students/delete/{id}", studentId))
                .andExpect(model().attributeExists("message"));

        verify(studentService, times(1)).deleteById(idCaptor.capture());
        assertEquals(studentId, idCaptor.getValue());
    }
}
