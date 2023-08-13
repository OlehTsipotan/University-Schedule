package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import com.university.schedule.service.GroupService;
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

@WebMvcTest(GroupController.class)
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Test
    public void getAll() throws Exception {
        List<Group> groups = new ArrayList<>();
        Discipline discipline = new Discipline("Discipline A");
        groups.add(new Group(1L, "Group A", discipline));
        groups.add(new Group(2L, "Group B", discipline));

        when(groupService.findAll(any())).thenReturn(groups);

        mockMvc.perform(MockMvcRequestBuilders.get("/groups"))
                .andExpect(status().isOk())
                .andExpect(view().name("groups"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", groups));
    }

    @Test
    public void delete() throws Exception {
        Long groupId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/groups/delete/{id}", groupId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groups")); // Check if redirected back to /groups

        verify(groupService, times(1)).deleteById(idCaptor.capture());
        assertEquals(groupId, idCaptor.getValue());
    }

    @Test
    public void delete_whenGroupServiceThrowsServiceException_thenRedirect() throws Exception {
        Long groupId = 1L;

        doThrow(new ServiceException("Delete error")).when(groupService).deleteById(groupId);

        mockMvc.perform(MockMvcRequestBuilders.get("/groups/delete/{id}", groupId))
                .andExpect(model().attributeExists("message"));

        verify(groupService, times(1)).deleteById(idCaptor.capture());
        assertEquals(groupId, idCaptor.getValue());
    }
}
