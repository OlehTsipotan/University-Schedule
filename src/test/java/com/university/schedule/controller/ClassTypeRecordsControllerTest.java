package com.university.schedule.controller;

import com.university.schedule.config.WebTestConfig;
import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.ClassTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
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
@Import(WebTestConfig.class)
@ActiveProfiles("test")
public class ClassTypeRecordsControllerTest {

    private static final String USERNAME = "testUsername";
    private static final String VIEW_AUTHORITY = "VIEW_CLASSTYPES";
    private static final String EDIT_AUTHORITY = "EDIT_CLASSTYPES";
    private static final String INSERT_AUTHORITY = "INSERT_CLASSTYPES";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ClassTypeService classTypeService;

    @Test
    @WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
    public void getAll_whenNoArgs_happyPath() throws Exception {
        List<ClassTypeDTO> classTypeList = new ArrayList<>();
        classTypeList.add(new ClassTypeDTO(1L, "Type 1"));
        classTypeList.add(new ClassTypeDTO(2L, "Type 2"));

        when(classTypeService.findAllAsDTO(any())).thenReturn(classTypeList);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtypes?offset=5")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("classtypes")).andExpect(
                model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
                    "reverseSortDirection")).andExpect(model().attribute("entities", classTypeList));

        verify(classTypeService, times(1)).findAllAsDTO(any());
    }

    @ParameterizedTest
    @CsvSource({"5, 10"})
    @WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
    public void getAll_whenLimitAndOffsetArgs_happyPath(int limit, int offset) throws Exception {
        List<ClassTypeDTO> classTypeList = new ArrayList<>();
        classTypeList.add(new ClassTypeDTO(1L, "Type 1"));
        classTypeList.add(new ClassTypeDTO(2L, "Type 2"));

        when(classTypeService.findAllAsDTO(any())).thenReturn(classTypeList);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/classtypes?offset=%d&limit=%d", offset, limit)))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("classtypes")).andExpect(
                model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
                    "reverseSortDirection")).andExpect(model().attribute("entities", classTypeList));

        verify(classTypeService, times(1)).findAllAsDTO(
            argThat(pageable -> pageable.getOffset() == offset && pageable.getPageSize() == limit));
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void delete_happyPath() throws Exception {
        Long classTypeId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/classtypes/delete/{id}", classTypeId))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/classtypes"));

        verify(classTypeService, times(1)).deleteById(classTypeId);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void delete_whenClassTypeServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
        Long classTypeId = 1L;
        String exceptionMessage = "Delete Error";

        doThrow(new DeletionFailedException(exceptionMessage)).when(classTypeService).deleteById(classTypeId);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtypes/delete/{id}", classTypeId))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classTypeService, times(1)).deleteById(classTypeId);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void getUpdateForm_happyPath() throws Exception {
        Long classTypeId = 1L;
        ClassTypeDTO classTypeDTO = new ClassTypeDTO(1L, "ClassType 1");

        when(classTypeService.findByIdAsDTO(classTypeId)).thenReturn(classTypeDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtypes/update/{id}", classTypeId)).andExpect(status().isOk())
            .andExpect(model().attributeExists("entity")).andExpect(view().name("classtypesUpdateForm"))
            .andExpect(model().attribute("entity", classTypeDTO));

        verify(classTypeService, times(1)).findByIdAsDTO(classTypeId);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void getUpdateForm_whenClassTypeNoFoundClassTypeServiceThrowsServiceException_thenProcessError()
        throws Exception {
        Long classTypeId = 1L;

        String exceptionMessage = "Not found";
        when(classTypeService.findByIdAsDTO(classTypeId)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/classtypes/update/{id}", classTypeId))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classTypeService, times(1)).findByIdAsDTO(classTypeId);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenValidClassType_thenRedirectSuccess() throws Exception {
        Long classTypeId = 1L;
        ClassTypeDTO classTypeDTO = new ClassTypeDTO(1L, "Test Class Type");

        mockMvc.perform(MockMvcRequestBuilders.post("/classtypes/update/{id}", classTypeId).with(csrf())
                .flashAttr("classTypeDTO", classTypeDTO)).andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/classtypes/update/" + classTypeId));

        verify(classTypeService, times(1)).save(classTypeDTO);
        verify(classTypeService, times(0)).findByIdAsDTO(any());
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenNotValidClassTypeByEmptyField_thenProcessForm() throws Exception {
        Long classTypeId = 1L;
        ClassTypeDTO classTypeDTO = ClassTypeDTO.builder().id(1L).name("") // Providing an empty name
            .build();

        when(classTypeService.findByIdAsDTO(classTypeId)).thenReturn(classTypeDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/classtypes/update/{id}", classTypeId).with(csrf())
                .flashAttr("classTypeDTO", classTypeDTO)).andExpect(status().is2xxSuccessful())
            .andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", classTypeDTO))
            .andExpect(view().name("classtypesUpdateForm"));

        verify(classTypeService, times(0)).save(any(ClassTypeDTO.class));
        verify(classTypeService, times(1)).findByIdAsDTO(classTypeId);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenClassTypeServiceThrowValidationException_thenProcessForm() throws Exception {
        Long classTypeId = 1L;
        ClassTypeDTO classTypeDTO = new ClassTypeDTO(1L, "Test Type");

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(classTypeService.save(classTypeDTO)).thenThrow(validationException);

        mockMvc.perform(MockMvcRequestBuilders.post("/classtypes/update/{id}", classTypeId).with(csrf())
            .flashAttr("classTypeDTO", classTypeDTO)).andExpect(status().is3xxRedirection());

        verify(classTypeService, times(1)).save(classTypeDTO);
        verifyNoMoreInteractions(classTypeService);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenClassTypeServiceThrowServiceException_processError() throws Exception {
        Long classTypeId = 1L;
        ClassTypeDTO classTypeDTO = new ClassTypeDTO(1L, "Test Type");

        String exceptionMessage = "Service Exception";

        when(classTypeService.save(classTypeDTO)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(MockMvcRequestBuilders.post("/classtypes/update/{id}", classTypeId).with(csrf())
                .flashAttr("classTypeDTO", classTypeDTO)).andExpect(status().is2xxSuccessful())
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classTypeService, times(1)).save(classTypeDTO);
        verifyNoMoreInteractions(classTypeService);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void getInsertForm_happyPath() throws Exception {
        mockMvc.perform(get("/classtypes/insert")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("classtypesInsertForm"));
    }


    @Test
    @WithMockUser(username = USERNAME)
    public void getInsertFrom_whenAccessDenied_processError() throws Exception {
        mockMvc.perform(get("/classtypes/insert")).andExpect(status().is2xxSuccessful())
            .andExpect(model().attributeExists("exceptionMessage")).andExpect(view().name("error"));
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenValidClassType_thenRedirectSuccess() throws Exception {
        Long classTypeId = 1L;
        ClassTypeDTO classTypeDTO = new ClassTypeDTO(null, "Class Type A");

        when(classTypeService.save(classTypeDTO)).thenReturn(classTypeId);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classtypes/insert").with(csrf()).flashAttr("classTypeDTO", classTypeDTO))
            .andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/classtypes"))
            .andExpect(flash().attribute("insertedSuccessId", classTypeId));

        verify(classTypeService, times(1)).save(classTypeDTO);
        verifyNoMoreInteractions(classTypeService);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenNotValidClassTypeByEmptyField_thenProcessForm() throws Exception {
        ClassTypeDTO classTypeDTO = new ClassTypeDTO(null, "");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classtypes/insert").with(csrf()).flashAttr("classTypeDTO", classTypeDTO))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("classtypesInsertForm"));

        verifyNoInteractions(classTypeService);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenClassTypeServiceThrowValidationException_thenProcessForm() throws Exception {
        ClassTypeDTO classTypeDTO = new ClassTypeDTO(null, "Test Class Type");

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(classTypeService.save(classTypeDTO)).thenThrow(validationException);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classtypes/insert").with(csrf()).flashAttr("classTypeDTO", classTypeDTO))
            .andExpect(status().is3xxRedirection());

        verify(classTypeService, times(1)).save(classTypeDTO);
        verifyNoMoreInteractions(classTypeService);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenClassTypeServiceThrowServiceException_processError() throws Exception {
        ClassTypeDTO classTypeDTO = new ClassTypeDTO(null, "Test Class Type");

        String exceptionMessage = "Service Exception";

        when(classTypeService.save(classTypeDTO)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classtypes/insert").with(csrf()).flashAttr("classTypeDTO", classTypeDTO))
            .andExpect(status().is2xxSuccessful()).andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classTypeService, times(1)).save(classTypeDTO);
        verifyNoMoreInteractions(classTypeService);
    }

}
