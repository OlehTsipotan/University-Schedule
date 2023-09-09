package com.university.schedule.controller;

import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.dto.TeacherUpdateDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.*;
import com.university.schedule.service.*;
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

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherRecordsController.class)
public class TeacherRecordsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private TeacherUpdateDTOService teacherUpdateDTOService;

    @MockBean
    private

    TeacherDTOService teacherDTOService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private CourseService courseService;

    @Test
    @WithMockUser(username = "username", authorities = {"VIEW_TEACHERS"})
    public void getAll_processPage() throws Exception {
        // Create a list of TeacherDTOs for testing
        List<TeacherDTO> teacherDTOs = new ArrayList<>();
        teacherDTOs.add(new TeacherDTO(1L, "email 1", "password 1", "firstName 1",
                "lastName 1", List.of("course 1"), true));
        teacherDTOs.add(new TeacherDTO(2L, "email 2", "password 2", "firstName 2",
                "lastName 2", List.of("course 2"), true));

        // Mock the behavior of teacherDTOService
        when(teacherDTOService.findAll(any(Pageable.class))).thenReturn(teacherDTOs);

        // Perform a GET request to /teachers and verify the result
        mockMvc.perform(MockMvcRequestBuilders.get("/teachers"))
                .andExpect(status().isOk())
                .andExpect(view().name("teachers"))
                .andExpect(model().attributeExists("teachers", "currentLimit", "currentOffset",
                        "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("teachers", teacherDTOs));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_TEACHERS"})
    public void delete() throws Exception {
        Long teacherId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/teachers/delete/{id}", teacherId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers"));

        verify(teacherService, times(1)).deleteById(teacherId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_TEACHERS"})
    public void getUpdateForm() throws Exception {
        Long teacherId = 1L;

        Course course = new Course(1L, "Group A");
        List<Course> courseList = new ArrayList<>();
        courseList.add(course);


        Role role = new Role(1L, "Role A");
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        TeacherUpdateDTO teacherUpdateDTO = new TeacherUpdateDTO(1L, "email 1", "firstName 1",
                "lastName 1", courseList, role, true);

        when(teacherUpdateDTOService.findById(teacherId)).thenReturn(teacherUpdateDTO);
        when(courseService.findAll()).thenReturn(courseList);
        when(roleService.findAll()).thenReturn(roles);

        mockMvc.perform(MockMvcRequestBuilders.get("/teachers/update/{id}", teacherId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity", "courses", "roles"))
                .andExpect(view().name("teachersUpdateForm"))
                .andExpect(model().attribute("entity", teacherUpdateDTO))
                .andExpect(model().attribute("courses", courseList))
                .andExpect(model().attribute("roles", roles));

        verify(teacherUpdateDTOService, times(1)).findById(teacherId);
        verify(courseService, times(1)).findAll();
        verify(roleService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_TEACHERS"})
    public void update_whenNotValidTeacherUpdateDTO_emptyField_thenProcessForm() throws Exception {
        Long teacherId = 1L;

        Course course = new Course(1L, "Group A");
        List<Course> courseList = new ArrayList<>();
        courseList.add(course);


        Role role = new Role(1L, "Role A");
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        TeacherUpdateDTO teacherUpdateDTO = new TeacherUpdateDTO(1L, "", "firstName 1",
                "lastName 1", courseList, role, true);

        when(teacherUpdateDTOService.findById(teacherId)).thenReturn(teacherUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/teachers/update/{id}", teacherId)
                        .param("isEnable", "false") // Add any other request parameters as needed
                        .with(csrf())
                        .flashAttr("teacherUpdateDTO", teacherUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", teacherUpdateDTO))
                .andExpect(view().name("teachersUpdateForm"));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_TEACHERS"})
    public void update_whenTeacherUpdateDTOServiceThrowValidationException_thenProcessForm() throws Exception {
        Long teacherId = 1L;

        Course course = new Course(1L, "Group A");
        List<Course> courseList = new ArrayList<>();
        courseList.add(course);


        Role role = new Role(1L, "Role A");
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        TeacherUpdateDTO teacherUpdateDTO = new TeacherUpdateDTO(1L, "email 1", "firstName 1",
                "lastName 1", courseList, role, true);

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(teacherUpdateDTOService.save(any())).thenThrow(validationException);
        when(teacherUpdateDTOService.findById(teacherId)).thenReturn(teacherUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/teachers/update/{id}", teacherId)
                        .param("isEnable", "false") // Add any other request parameters as needed
                        .with(csrf())
                        .flashAttr("teacherUpdateDTO", teacherUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("validationServiceErrors"))
                .andExpect(model().attribute("validationServiceErrors", validationException.getViolations()))
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", teacherUpdateDTO))
                .andExpect(view().name("teachersUpdateForm"));

        verify(teacherUpdateDTOService, times(1)).save(teacherUpdateDTO);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_TEACHERS"})
    public void update_whenTeacherUpdateDTOServiceThrowServiceException_thenProcessForm() throws Exception {
        Long teacherId = 1L;

        Course course = new Course(1L, "Group A");
        List<Course> courseList = new ArrayList<>();
        courseList.add(course);


        Role role = new Role(1L, "Role A");
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        TeacherUpdateDTO teacherUpdateDTO = new TeacherUpdateDTO(1L, "email 1", "firstName 1",
                "lastName 1", courseList, role, true);

        ServiceException serviceException = new ServiceException("testException");

        when(teacherUpdateDTOService.save(any())).thenThrow(serviceException);
        when(teacherUpdateDTOService.findById(teacherId)).thenReturn(teacherUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/teachers/update/{id}", teacherId)
                        .param("isEnable", "false") // Add any other request parameters as needed
                        .with(csrf())
                        .flashAttr("teacherUpdateDTO", teacherUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("serviceError"))
                .andExpect(model().attribute("serviceError", serviceException.getMessage()))
                .andExpect(model().attribute("entity", teacherUpdateDTO))
                .andExpect(view().name("teachersUpdateForm"));
    }
}
