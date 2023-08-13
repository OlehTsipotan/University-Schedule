package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.User;
import com.university.schedule.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Test
    public void getAll() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "user1@example.com", "password1", "John", "Doe"));
        users.add(new User(2L, "user2@example.com", "password2", "Jane", "Smith"));

        when(userService.findAll(any())).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", users));
    }

    @Test
    public void delete() throws Exception {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/delete/{id}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users")); // Check if redirected back to /users

        verify(userService, times(1)).deleteById(idCaptor.capture());
        assertEquals(userId, idCaptor.getValue());
    }

    @Test
    public void delete_whenUserServiceThrowsServiceException_thenRedirect() throws Exception {
        Long userId = 1L;

        doThrow(new ServiceException("Delete error")).when(userService).deleteById(userId);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/delete/{id}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users")); // Check if redirected back to /users

        verify(userService, times(1)).deleteById(idCaptor.capture());
        assertEquals(userId, idCaptor.getValue());
    }
}
