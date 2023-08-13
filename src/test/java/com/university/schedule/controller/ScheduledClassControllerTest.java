package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.*;
import com.university.schedule.service.ScheduledClassService;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduledClassController.class)
public class ScheduledClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduledClassService scheduledClassService;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Test
    public void getAll() throws Exception {
        List<ScheduledClass> scheduledClasses = new ArrayList<>();

        Course course = new Course("Course A");
        Teacher teacher = new Teacher("Email A", "Password A",
                "First Name A", "Last Name A");
        ClassTime classTime = new ClassTime(1, LocalTime.of(8, 30), Duration.ofMinutes(95));
        ClassType classType = new ClassType("Lecture");
        Building building = new Building("Building A", "Address A");
        Classroom classroom = new Classroom("Classroom A", building);
        Set<Group> groups = new HashSet<>();
        Discipline discipline = new Discipline("Discipline A");
        groups.add(new Group("Group A", discipline));

        scheduledClasses.add(
                ScheduledClass.builder()
                        .id(1L)
                        .course(course)
                        .teacher(teacher)
                        .classTime(classTime)
                        .classType(classType)
                        .classroom(classroom)
                        .groups(groups)
                        .date(LocalDate.now())
                        .build()
        );
        scheduledClasses.add(
                ScheduledClass.builder()
                        .id(2L)
                        .course(course)
                        .teacher(teacher)
                        .classTime(classTime)
                        .classType(classType)
                        .classroom(classroom)
                        .groups(groups)
                        .date(LocalDate.now())
                        .build()
        );

        when(scheduledClassService.findAll(any())).thenReturn(scheduledClasses);

        mockMvc.perform(MockMvcRequestBuilders.get("/classes"))
                .andExpect(status().isOk())
                .andExpect(view().name("classes"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", scheduledClasses));
    }

    @Test
    public void delete() throws Exception {
        Long scheduledClassId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/classes/delete/{id}", scheduledClassId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classes")); // Check if redirected back to /classes

        verify(scheduledClassService, times(1)).deleteById(idCaptor.capture());
        assertEquals(scheduledClassId, idCaptor.getValue());
    }

    @Test
    public void delete_whenScheduledClassServiceThrowsServiceException_thenRedirect() throws Exception {
        Long scheduledClassId = 1L;

        doThrow(new ServiceException("Delete error")).when(scheduledClassService).deleteById(scheduledClassId);

        mockMvc.perform(MockMvcRequestBuilders.get("/classes/delete/{id}", scheduledClassId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classes")); // Check if redirected back to /classes

        verify(scheduledClassService, times(1)).deleteById(idCaptor.capture());
        assertEquals(scheduledClassId, idCaptor.getValue());
    }
}
