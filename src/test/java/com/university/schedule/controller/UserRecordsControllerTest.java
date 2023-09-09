package com.university.schedule.controller;

import com.university.schedule.dto.UserUpdateDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Role;
import com.university.schedule.model.User;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.UserService;
import com.university.schedule.service.UserUpdateDTOService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(UserRecordsController.class)
public class UserRecordsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserUpdateDTOService userUpdateDTOService;

    @MockBean
    private RoleService roleService;

    @Test
    @WithMockUser(username = "username", authorities = {"VIEW_USERS"})
    public void getAll_processPage() throws Exception {
        // Create a list of User objects for testing
        List<User> users = new ArrayList<>();
        Role role = new Role(1L, "Teacher");
        users.add(new User(1L, "email 1", "password 1", "firstName 1",
                "lastName 1", role, true));
        users.add(new User(2L, "email 2", "password 2", "firstName 2",
                "lastName 2", role, true));

        // Mock the behavior of userService
        when(userService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(users));

        // Perform a GET request to /users and verify the result
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("entities", "currentLimit", "currentOffset",
                        "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", users));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_USERS"})
    public void delete() throws Exception {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/delete/{id}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService, times(1)).deleteById(userId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_USERS"})
    public void getUpdateForm() throws Exception {
        Long userId = 1L;

        Role role = new Role(1L, "Teacher");

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(1L, "email 1", "firstName 1",
                "lastName 1", role, true);

        List<Role> roles = new ArrayList<>();
        roles.add(role);

        when(userUpdateDTOService.findById(userId)).thenReturn(userUpdateDTO);
        when(roleService.findAll()).thenReturn(roles);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/update/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity", "roles"))
                .andExpect(view().name("usersUpdateForm"))
                .andExpect(model().attribute("entity", userUpdateDTO))
                .andExpect(model().attribute("roles", roles));

        verify(userUpdateDTOService, times(1)).findById(userId);
        verify(roleService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_USERS"})
    public void update_whenNotValidUserUpdateDTO_emptyField_thenProcessForm() throws Exception {
        Long userId = 1L;
        Role role = new Role(1L, "Teacher");
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(1L, "", "firstName 1",
                "lastName 1", role, true); // Empty fields

        when(userUpdateDTOService.findById(userId)).thenReturn(userUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/update/{id}", userId)
                        .param("isEnable", "false") // Add any other request parameters as needed
                        .with(csrf())
                        .flashAttr("userUpdateDTO", userUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", userUpdateDTO))
                .andExpect(view().name("usersUpdateForm"));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_USERS"})
    public void update_whenUserUpdateDTOServiceThrowValidationException_thenProcessForm() throws Exception {
        Long userId = 1L;
        Role role = new Role(1L, "Teacher");
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(1L, "email 1", "firstName 1",
                "lastName 1", role, true); // Empty fields

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(userUpdateDTOService.save(any())).thenThrow(validationException);
        when(userUpdateDTOService.findById(userId)).thenReturn(userUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/update/{id}", userId)
                        .param("isEnable", "false") // Add any other request parameters as needed
                        .with(csrf())
                        .flashAttr("userUpdateDTO", userUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("validationServiceErrors"))
                .andExpect(model().attribute("validationServiceErrors", validationException.getViolations()))
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", userUpdateDTO))
                .andExpect(view().name("usersUpdateForm"));

        verify(userUpdateDTOService, times(1)).save(userUpdateDTO);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_USERS"})
    public void update_whenUserUpdateDTOServiceThrowServiceException_thenProcessForm() throws Exception {
        Long userId = 1L;
        Role role = new Role(1L, "Teacher");
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(1L, "email 1", "firstName 1",
                "lastName 1", role, true); // Empty fields

        ServiceException serviceException = new ServiceException("testException");

        when(userUpdateDTOService.save(any())).thenThrow(serviceException);
        when(userUpdateDTOService.findById(userId)).thenReturn(userUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/update/{id}", userId)
                        .param("isEnable", "false") // Add any other request parameters as needed
                        .with(csrf())
                        .flashAttr("userUpdateDTO", userUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("serviceError"))
                .andExpect(model().attribute("serviceError", serviceException.getMessage()))
                .andExpect(model().attribute("entity", userUpdateDTO))
                .andExpect(view().name("usersUpdateForm"));
    }
}
