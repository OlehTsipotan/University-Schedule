package com.university.schedule.controller;

import com.university.schedule.config.WebTestConfig;
import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.ClassTimeService;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassTimeRecordsController.class)
@Import(WebTestConfig.class)
@ActiveProfiles("test")
public class ClassTimeRecordsControllerTest {

    public static final String USERNAME = "testUsername";
    public static final String VIEW_AUTHORITY = "VIEW_CLASSTIMES";
    public static final String EDIT_AUTHORITY = "EDIT_CLASSTIMES";
    public static final String INSERT_AUTHORITY = "INSERT_CLASSTIMES";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ClassTimeService classTimeService;

    @Test
    @WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
    public void getAll_whenNoArgs_happyPath() throws Exception {
        List<ClassTimeDTO> classTimeList = new ArrayList<>();
        classTimeList.add(new ClassTimeDTO(1L, 1, null, 60));
        classTimeList.add(new ClassTimeDTO(2L, 2, null, 60));

        when(classTimeService.findAllAsDTO(any())).thenReturn(classTimeList);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtimes?offset=5")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("classtimes")).andExpect(
                model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
                    "reverseSortDirection")).andExpect(model().attribute("entities", classTimeList));

        verify(classTimeService, times(1)).findAllAsDTO(any());
    }

    @ParameterizedTest
    @CsvSource({"5, 10"})
    @WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
    public void getAll_whenLimitAndOffsetArgs_happyPath(int limit, int offset) throws Exception {
        List<ClassTimeDTO> classTimeList = new ArrayList<>();
        classTimeList.add(new ClassTimeDTO(1L, 1, null, 60));
        classTimeList.add(new ClassTimeDTO(2L, 2, null, 60));

        when(classTimeService.findAllAsDTO(any())).thenReturn(classTimeList);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/classtimes?offset=%d&limit=%d", offset, limit)))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("classtimes")).andExpect(
                model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
                    "reverseSortDirection")).andExpect(model().attribute("entities", classTimeList));

        verify(classTimeService, times(1)).findAllAsDTO(
            argThat(pageable -> pageable.getOffset() == offset && pageable.getPageSize() == limit));
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void delete_happyPath() throws Exception {
        Long classTimeId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/classtimes/delete/{id}", classTimeId))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/classtimes"));

        verify(classTimeService, times(1)).deleteById(classTimeId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void delete_whenClassTimeServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
        Long classTimeId = 1L;
        String exceptionMessage = "Delete Error";

        doThrow(new DeletionFailedException(exceptionMessage)).when(classTimeService).deleteById(classTimeId);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtimes/delete/{id}", classTimeId))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classTimeService, times(1)).deleteById(classTimeId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void getUpdateForm_happyPath() throws Exception {
        Long classTimeId = 1L;
        ClassTimeDTO classTimeDTO = new ClassTimeDTO(1L, 1, null, 60);

        when(classTimeService.findByIdAsDTO(classTimeId)).thenReturn(classTimeDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/classtimes/update/{id}", classTimeId)).andExpect(status().isOk())
            .andExpect(model().attributeExists("entity")).andExpect(view().name("classtimesUpdateForm"))
            .andExpect(model().attribute("entity", classTimeDTO));

        verify(classTimeService, times(1)).findByIdAsDTO(classTimeId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void getUpdateForm_whenClassTimeNoFoundClassTimeServiceThrowsServiceException_thenProcessError()
        throws Exception {
        Long classTimeId = 1L;

        String exceptionMessage = "Not found";
        when(classTimeService.findByIdAsDTO(classTimeId)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/classtimes/update/{id}", classTimeId))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classTimeService, times(1)).findByIdAsDTO(classTimeId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenValidClassTime_thenRedirectSuccess() throws Exception {
        Long classTimeId = 1L;
        ClassTimeDTO classTimeDTO = new ClassTimeDTO(1L, 1, LocalTime.now(), 60);

        mockMvc.perform(MockMvcRequestBuilders.post("/classtimes/update/{id}", classTimeId).with(csrf())
                .flashAttr("classTimeDTO", classTimeDTO)).andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/classtimes/update/" + classTimeId));

        verify(classTimeService, times(1)).save(classTimeDTO);
        verify(classTimeService, times(0)).findByIdAsDTO(any());
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenNotValidClassTimeByEmptyField_thenProcessForm() throws Exception {
        Long classTimeId = 1L;
        ClassTimeDTO classTimeDTO = new ClassTimeDTO(1L, 1, null, 60);
        when(classTimeService.findByIdAsDTO(classTimeId)).thenReturn(classTimeDTO);


        mockMvc.perform(MockMvcRequestBuilders.post("/classtimes/update/{id}", classTimeId).with(csrf())
                .flashAttr("classTimeDTO", classTimeDTO)).andExpect(status().is2xxSuccessful())
            .andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", classTimeDTO))
            .andExpect(view().name("classtimesUpdateForm"));

        verify(classTimeService, times(0)).save((ClassTimeDTO) any());
        verify(classTimeService, times(1)).findByIdAsDTO(classTimeId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenClassTimeServiceThrowValidationException_thenProcessForm() throws Exception {
        Long classTimeId = 1L;
        ClassTimeDTO classTimeDTO = new ClassTimeDTO(1L, 1, LocalTime.now(), 60);

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(classTimeService.save(classTimeDTO)).thenThrow(validationException);

        mockMvc.perform(MockMvcRequestBuilders.post("/classtimes/update/{id}", classTimeId).with(csrf())
            .flashAttr("classTimeDTO", classTimeDTO)).andExpect(status().is3xxRedirection());

        verify(classTimeService, times(1)).save(classTimeDTO);
        verify(classTimeService, times(0)).findByIdAsDTO(any());
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenClassTimeServiceThrowServiceException_processError() throws Exception {
        Long classTimeId = 1L;
        ClassTimeDTO classTimeDTO = new ClassTimeDTO(1L, 1, LocalTime.now(), 60);
        String exceptionMessage = "Service Exception";

        when(classTimeService.save(classTimeDTO)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(MockMvcRequestBuilders.post("/classtimes/update/{id}", classTimeId).with(csrf())
                .flashAttr("classTimeDTO", classTimeDTO)).andExpect(status().is2xxSuccessful())
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classTimeService, times(1)).save(classTimeDTO);
        verify(classTimeService, times(0)).findByIdAsDTO(any());
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void getInsertForm_happyPath() throws Exception {
        mockMvc.perform(get("/classtimes/insert")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("classtimesInsertForm"));
    }

    @Test
    @WithMockUser(username = USERNAME)
    public void getInsertFrom_whenAccessDenied_processError() throws Exception {
        mockMvc.perform(get("/classtimes/insert")).andExpect(status().is2xxSuccessful())
            .andExpect(model().attributeExists("exceptionMessage")).andExpect(view().name("error"));
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenValidClassTime_thenRedirectSuccess() throws Exception {
        Long classTimeId = 0L;
        ClassTimeDTO classTimeDTO = new ClassTimeDTO(null, 1, LocalTime.now(), 60);

        when(classTimeService.save(classTimeDTO)).thenReturn(classTimeId);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classtimes/insert").with(csrf()).flashAttr("classTimeDTO", classTimeDTO))
            .andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/classtimes"))
            .andExpect(flash().attribute("insertedSuccessId", classTimeId));

        verify(classTimeService, times(1)).save(classTimeDTO);
        verifyNoMoreInteractions(classTimeService);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenNotValidClassTimeByEmptyField_thenProcessForm() throws Exception {
        ClassTimeDTO classTimeDTO = new ClassTimeDTO(null, 1, null, 60);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classtimes/insert").with(csrf()).flashAttr("classTimeDTO", classTimeDTO))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("classtimesInsertForm"));

        verifyNoInteractions(classTimeService);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenClassTimeServiceThrowValidationException_thenProcessForm() throws Exception {
        ClassTimeDTO classTimeDTO = new ClassTimeDTO(null, 1, LocalTime.now(), 60);

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(classTimeService.save(classTimeDTO)).thenThrow(validationException);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classtimes/insert").with(csrf()).flashAttr("classTimeDTO", classTimeDTO))
            .andExpect(status().is3xxRedirection());

        verify(classTimeService, times(1)).save(classTimeDTO);
        verifyNoMoreInteractions(classTimeService);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenClassTimeServiceThrowServiceException_processError() throws Exception {
        ClassTimeDTO classTimeDTO = new ClassTimeDTO(null, 1, LocalTime.now(), 60);

        String exceptionMessage = "Service Exception";

        when(classTimeService.save(classTimeDTO)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classtimes/insert").with(csrf()).flashAttr("classTimeDTO", classTimeDTO))
            .andExpect(status().is2xxSuccessful()).andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classTimeService, times(1)).save(classTimeDTO);
        verifyNoMoreInteractions(classTimeService);
    }
}
