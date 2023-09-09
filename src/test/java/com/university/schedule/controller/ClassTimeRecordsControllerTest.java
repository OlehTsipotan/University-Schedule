package com.university.schedule.controller;


import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.dto.ClassTimeUpdateDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ClassTime;
import com.university.schedule.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassTimeRecordsController.class)
public class ClassTimeRecordsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassTimeDTOService classTimeDTOService;

    @MockBean
    private ClassTimeService classTimeService;

    @MockBean
    private ClassTimeUpdateDTOService classTimeUpdateDTOService;

    @Test
    @WithMockUser(username = "username", authorities = {"VIEW_CLASSTIMES"})
    public void getAll_processPage() throws Exception {
        List<ClassTimeDTO> classTimeDTOs = new ArrayList<>();
        classTimeDTOs.add(new ClassTimeDTO(1L, 1, LocalTime.of(9, 0).toString(), 95));
        classTimeDTOs.add(new ClassTimeDTO(2L, 2, LocalTime.of(11, 0).toString(), 95));

        when(classTimeDTOService.findAll(any(Pageable.class))).thenReturn(classTimeDTOs);

        mockMvc.perform(get("/classtimes"))
                .andExpect(status().isOk())
                .andExpect(view().name("classtimes"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", classTimeDTOs));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSTIMES"})
    public void delete() throws Exception {
        Long classTimeId = 1L;

        mockMvc.perform(get("/classtimes/delete/{id}", classTimeId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classtimes"));

        verify(classTimeService, times(1)).deleteById(classTimeId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSTIMES"})
    public void getUpdateForm() throws Exception {
        Long classTimeId = 1L;
        ClassTimeUpdateDTO classTimeUpdateDTO = new ClassTimeUpdateDTO(classTimeId, 1, LocalTime.of(9, 0), 95);

        when(classTimeUpdateDTOService.findById(classTimeId)).thenReturn(classTimeUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtimes/update/{id}", classTimeId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(view().name("classtimesUpdateForm"))
                .andExpect(model().attribute("entity", classTimeUpdateDTO));

        verify(classTimeUpdateDTOService, times(1)).findById(classTimeId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSTIMES"})
    public void update_whenNotValidClassTime_emptyField_thenProcessForm() throws Exception {
        Long classTimeId = 1L;

        ClassTimeUpdateDTO classTimeUpdateDTO = new ClassTimeUpdateDTO(classTimeId, 1, null, 95);

        when(classTimeUpdateDTOService.findById(classTimeId)).thenReturn(classTimeUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/classtimes/update/{id}", classTimeId)
                        .with(csrf())
                        .flashAttr("classTimeUpdateDTO", classTimeUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", classTimeUpdateDTO))
                .andExpect(view().name("classtimesUpdateForm"));

        verify(classTimeService, times(0)).save((ClassTime) any());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSTIMES"})
    public void update_whenClassTimeServiceThrowValidationException_thenProcessForm() throws Exception {
        Long classTimeId = 1L;

        ClassTimeUpdateDTO classTimeUpdateDTO = new ClassTimeUpdateDTO(classTimeId, 1, LocalTime.of(9, 0), 95);

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(classTimeUpdateDTOService.save(any())).thenThrow(validationException);
        when(classTimeUpdateDTOService.findById(classTimeId)).thenReturn(classTimeUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/classtimes/update/{id}", classTimeId)
                        .with(csrf())
                        .flashAttr("classTimeUpdateDTO", classTimeUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("validationServiceErrors"))
                .andExpect(model().attribute("validationServiceErrors", validationException.getViolations()))
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", classTimeUpdateDTO))
                .andExpect(view().name("classtimesUpdateForm"));

        verify(classTimeUpdateDTOService, times(1)).save(classTimeUpdateDTO);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSTIMES"})
    public void update_whenClassTimeServiceThrowServiceException_thenProcessForm() throws Exception {
        Long classTimeId = 1L;

        ClassTimeUpdateDTO classTimeUpdateDTO = new ClassTimeUpdateDTO(classTimeId, 1, LocalTime.of(9, 0), 95);

        ServiceException serviceException = new ServiceException("testException");

        when(classTimeUpdateDTOService.save(any())).thenThrow(serviceException);
        when(classTimeUpdateDTOService.findById(classTimeId)).thenReturn(classTimeUpdateDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/classtimes/update/{id}", classTimeId)
                        .with(csrf())
                        .flashAttr("classTimeUpdateDTO", classTimeUpdateDTO))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("serviceError"))
                .andExpect(model().attribute("serviceError", serviceException.getMessage()))
                .andExpect(model().attribute("entity", classTimeUpdateDTO))
                .andExpect(view().name("classtimesUpdateForm"));
    }
}

