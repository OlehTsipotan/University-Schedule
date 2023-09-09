package com.university.schedule.controller;

import com.university.schedule.dto.*;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentRecordsController.class)
public class StudentRecordsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private StudentUpdateDTOService studentUpdateDTOService;

    @MockBean
    private StudentDTOService studentDTOService;

    @MockBean
    private GroupService groupService;

    @MockBean
    private RoleService roleService;

    @Test
    @WithMockUser(username = "username", authorities = {"VIEW_STUDENTS"})
    public void getAll_processPage() throws Exception {
        // Create a list of StudentDTOs for testing
        List<StudentDTO> studentDTOs = new ArrayList<>();
        studentDTOs.add(new StudentDTO(1L, "email 1", "password 1", "firstName 1",
                "lastName 1", "group 1", true));
        studentDTOs.add(new StudentDTO(1L, "email 1", "password 1", "firstName 1",
                "lastName 1", "group 1", true));

        // Mock the behavior of studentDTOService
        when(studentDTOService.findAll(any(Pageable.class))).thenReturn(studentDTOs);

        // Perform a GET request to /students and verify the result
        mockMvc.perform(MockMvcRequestBuilders.get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students"))
                .andExpect(model().attributeExists("students", "currentLimit", "currentOffset",
                        "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("students", studentDTOs));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_STUDENTS"})
    public void delete() throws Exception {
        Long studentId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/students/delete/{id}", studentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        verify(studentService, times(1)).deleteById(studentId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_STUDENTS"})
    public void getUpdateForm() throws Exception {
        Long studentId = 1L;

        Group group = new Group(1L, "Group A", new Discipline(1L, "Discipline A"));
        List<Group> groups = new ArrayList<>();
        groups.add(group);


        Role role = new Role(1L, "Role A");
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        StudentUpdateDTO studentUpdateDTO = new StudentUpdateDTO(1L, "email 1", "firstName 1",
                "lastName 1", group, role, true);

        when(studentUpdateDTOService.findById(studentId)).thenReturn(studentUpdateDTO);
        when(groupService.findAll()).thenReturn(groups);
        when(roleService.findAll()).thenReturn(roles);

        mockMvc.perform(MockMvcRequestBuilders.get("/students/update/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity", "groups", "roles"))
                .andExpect(view().name("studentsUpdateForm"))
                .andExpect(model().attribute("entity", studentUpdateDTO))
                .andExpect(model().attribute("groups", groups))
                .andExpect(model().attribute("roles", roles));

        verify(studentUpdateDTOService, times(1)).findById(studentId);
        verify(groupService, times(1)).findAll();
        verify(roleService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_STUDENTS"})
    public void update_whenNotValidStudentUpdateDTO_emptyField_thenProcessForm() throws Exception {
        Long studentId = 1L;

        Group group = new Group(1L, "Group A", new Discipline(1L, "Discipline A"));

        Role role = new Role(1L, "Role A");

        StudentUpdateDTO studentUpdateDTO = new StudentUpdateDTO(1L, "", "firstName 1",
                "lastName 1", group, role, true);

        when(studentUpdateDTOService.findById(studentId)).thenReturn(studentUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/students/update/{id}", studentId)
                        .param("isEnable", "false") // Add any other request parameters as needed
                        .with(csrf())
                        .flashAttr("studentUpdateDTO", studentUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", studentUpdateDTO))
                .andExpect(view().name("studentsUpdateForm"));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_STUDENTS"})
    public void update_whenStudentUpdateDTOServiceThrowValidationException_thenProcessForm() throws Exception {
        Long studentId = 1L;

        Group group = new Group(1L, "Group A", new Discipline(1L, "Discipline A"));

        Role role = new Role(1L, "Role A");

        StudentUpdateDTO studentUpdateDTO = new StudentUpdateDTO(1L, "email 1", "firstName 1",
                "lastName 1", group, role, true);

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(studentUpdateDTOService.save(any())).thenThrow(validationException);
        when(studentUpdateDTOService.findById(studentId)).thenReturn(studentUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/students/update/{id}", studentId)
                        .param("isEnable", "false") // Add any other request parameters as needed
                        .with(csrf())
                        .flashAttr("studentUpdateDTO", studentUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("validationServiceErrors"))
                .andExpect(model().attribute("validationServiceErrors", validationException.getViolations()))
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", studentUpdateDTO))
                .andExpect(view().name("studentsUpdateForm"));

        verify(studentUpdateDTOService, times(1)).save(studentUpdateDTO);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_STUDENTS"})
    public void update_whenStudentUpdateDTOServiceThrowServiceException_thenProcessForm() throws Exception {
        Long studentId = 1L;

        Group group = new Group(1L, "Group A", new Discipline(1L, "Discipline A"));

        Role role = new Role(1L, "Role A");

        StudentUpdateDTO studentUpdateDTO = new StudentUpdateDTO(1L, "email 1", "firstName 1",
                "lastName 1", group, role, true);

        ServiceException serviceException = new ServiceException("testException");

        when(studentUpdateDTOService.save(any())).thenThrow(serviceException);
        when(studentUpdateDTOService.findById(studentId)).thenReturn(studentUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/students/update/{id}", studentId)
                        .param("isEnable", "false") // Add any other request parameters as needed
                        .with(csrf())
                        .flashAttr("studentUpdateDTO", studentUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("serviceError"))
                .andExpect(model().attribute("serviceError", serviceException.getMessage()))
                .andExpect(model().attribute("entity", studentUpdateDTO))
                .andExpect(view().name("studentsUpdateForm"));
    }
}
