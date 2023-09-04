package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Course;
import com.university.schedule.service.CourseService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseRecordsController.class)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Test
    public void getAll() throws Exception {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(1L, "Math"));
        courses.add(new Course(2L, "History"));

        when(courseService.findAll((Sort) any())).thenReturn(courses);

        mockMvc.perform(MockMvcRequestBuilders.get("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", courses));
    }

    @Test
    public void delete() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/courses/delete/{id}", courseId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses")); // Check if redirected back to /courses

        verify(courseService, times(1)).deleteById(idCaptor.capture());
        assertEquals(courseId, idCaptor.getValue());
    }

    @Test
    public void delete_whenCourseServiceThrowsServiceException_thenRedirect() throws Exception {
        Long courseId = 1L;

        doThrow(new ServiceException("Delete error")).when(courseService).deleteById(courseId);

        mockMvc.perform(MockMvcRequestBuilders.get("/courses/delete/{id}", courseId))
                .andExpect(model().attributeExists("message"));

        verify(courseService, times(1)).deleteById(idCaptor.capture());
        assertEquals(courseId, idCaptor.getValue());
    }
}
