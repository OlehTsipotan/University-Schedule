package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ClassType;
import com.university.schedule.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassTypeRecordsController.class)
public class ClassTypeRecordsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassTypeService classTypeService;

    @Test
    @WithMockUser(username = "username", authorities = {"VIEW_CLASSTYPES"})
    public void getAll_processPage() throws Exception {
        List<ClassType> classTypes = new ArrayList<>();
        classTypes.add(new ClassType(1L, "Type 1"));
        classTypes.add(new ClassType(2L, "Type 2"));

        when(classTypeService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(classTypes));

        mockMvc.perform(get("/classtypes"))
                .andExpect(status().isOk())
                .andExpect(view().name("classtypes"))
                .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
                .andExpect(model().attribute("entities", classTypes));
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSTYPES"})
    public void delete() throws Exception {
        Long classTypeId = 1L;

        mockMvc.perform(get("/classtypes/delete/{id}", classTypeId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classtypes"));

        verify(classTypeService, times(1)).deleteById(classTypeId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSTYPES"})
    public void getUpdateForm() throws Exception {
        Long classTypeId = 1L;
        ClassType classType = new ClassType(classTypeId, "Type 1");

        when(classTypeService.findById(classTypeId)).thenReturn(classType);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtypes/update/{id}", classTypeId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(view().name("classtypesUpdateForm"))
                .andExpect(model().attribute("entity", classType));

        verify(classTypeService, times(1)).findById(classTypeId);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSTYPES"})
    public void update_whenNotValidClassType_emptyField_thenProcessForm() throws Exception {
        Long classTypeId = 1L;

        ClassType classType = new ClassType(classTypeId, ""); // Empty name

        when(classTypeService.findById(classTypeId)).thenReturn(classType);

        mockMvc.perform(MockMvcRequestBuilders.post("/classtypes/update/{id}", classTypeId)
                        .with(csrf())
                        .flashAttr("classType", classType))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", classType))
                .andExpect(view().name("classtypesUpdateForm"));

        verify(classTypeService, times(0)).save((ClassType) any());
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSTYPES"})
    public void update_whenClassTypeServiceThrowValidationException_thenProcessForm() throws Exception {
        Long classTypeId = 1L;

        ClassType classType = new ClassType(classTypeId, "Type 1");

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(classTypeService.save(any())).thenThrow(validationException);
        when(classTypeService.findById(classTypeId)).thenReturn(classType);


        mockMvc.perform(MockMvcRequestBuilders.post("/classtypes/update/{id}", classTypeId)
                        .with(csrf())
                        .flashAttr("classType", classType))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("validationServiceErrors"))
                .andExpect(model().attribute("validationServiceErrors", validationException.getViolations()))
                .andExpect(model().attributeExists("entity"))
                .andExpect(model().attribute("entity", classType))
                .andExpect(view().name("classtypesUpdateForm"));

        verify(classTypeService, times(1)).save(classType);
    }

    @Test
    @WithMockUser(username = "username", authorities = {"EDIT_CLASSTYPES"})
    public void update_whenClassTypeServiceThrowServiceException_thenProcessForm() throws Exception {
        Long classTypeId = 1L;

        ClassType classType = new ClassType(classTypeId, "Type 1");

        ServiceException serviceException = new ServiceException("testException");

        when(classTypeService.save(any())).thenThrow(serviceException);
        when(classTypeService.findById(classTypeId)).thenReturn(classType);

        mockMvc.perform(MockMvcRequestBuilders.post("/classtypes/update/{id}", classTypeId)
                        .with(csrf())
                        .flashAttr("classType", classType))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("serviceError"))
                .andExpect(model().attribute("serviceError", serviceException.getMessage()))
                .andExpect(model().attribute("entity", classType))
                .andExpect(view().name("classtypesUpdateForm"));
    }
}
