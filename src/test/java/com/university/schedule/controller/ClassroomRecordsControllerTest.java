package com.university.schedule.controller;

import com.university.schedule.config.WebTestConfig;
import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.BuildingService;
import com.university.schedule.service.ClassroomService;
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

@WebMvcTest(ClassroomRecordsController.class)
@Import(WebTestConfig.class)
@ActiveProfiles("test")
public class ClassroomRecordsControllerTest {

    public static final String USERNAME = "testUsername";
    public static final String VIEW_AUTHORITY = "VIEW_CLASSROOMS";
    public static final String EDIT_AUTHORITY = "EDIT_CLASSROOMS";
    public static final String INSERT_AUTHORITY = "INSERT_CLASSROOMS";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassroomService classroomService;

    @MockBean
    private BuildingService buildingService;

    @Test
    @WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
    public void getAll_whenNoArgs_happyPath() throws Exception {
        List<ClassroomDTO> classroomList = new ArrayList<>();
        classroomList.add(new ClassroomDTO(1L, "Classroom A", new BuildingDTO(1L, "Building A", "Address A")));
        classroomList.add(new ClassroomDTO(2L, "Classroom B", new BuildingDTO(2L, "Building B", "Address B")));

        when(classroomService.findAllAsDTO(any())).thenReturn(classroomList);

        mockMvc.perform(MockMvcRequestBuilders.get("/classrooms?offset=5")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("classrooms"))
            .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
            .andExpect(model().attribute("entities", classroomList));

        verify(classroomService, times(1)).findAllAsDTO(any());
    }

    @ParameterizedTest
    @CsvSource({"5, 10"})
    @WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
    public void getAll_whenLimitAndOffsetArgs_happyPath(int limit, int offset) throws Exception {
        List<ClassroomDTO> classroomList = new ArrayList<>();
        classroomList.add(new ClassroomDTO(1L, "Classroom A", new BuildingDTO(1L, "Building A", "Address A")));
        classroomList.add(new ClassroomDTO(2L, "Classroom B", new BuildingDTO(2L, "Building B", "Address B")));

        when(classroomService.findAllAsDTO(any())).thenReturn(classroomList);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/classrooms?offset=%d&limit=%d", offset, limit)))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("classrooms"))
            .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
            .andExpect(model().attribute("entities", classroomList));

        verify(classroomService, times(1)).findAllAsDTO(
            argThat(pageable -> pageable.getOffset() == offset && pageable.getPageSize() == limit));
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void delete_happyPath() throws Exception {
        Long classroomId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/classrooms/delete/{id}", classroomId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/classrooms")); // Check if redirected back to /classrooms

        verify(classroomService, times(1)).deleteById(classroomId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void delete_whenClassroomServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
        Long classroomId = 1L;
        String exceptionMessage = "Delete Error";

        doThrow(new DeletionFailedException(exceptionMessage)).when(classroomService).deleteById(classroomId);

        mockMvc.perform(MockMvcRequestBuilders.get("/classrooms/delete/{id}", classroomId))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classroomService, times(1)).deleteById(classroomId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void getUpdateForm_happyPath() throws Exception {
        Long classroomId = 1L;
        ClassroomDTO classroomDTO = new ClassroomDTO(1L, "Classroom A", new BuildingDTO(1L, "Building A", "Address A"));

        when(classroomService.findByIdAsDTO(classroomId)).thenReturn(classroomDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/classrooms/update/{id}", classroomId)).andExpect(status().isOk())
            .andExpect(model().attributeExists("entity", "buildingDTOList"))
            .andExpect(view().name("classroomsUpdateForm")).andExpect(model().attribute("entity", classroomDTO));

        verify(classroomService, times(1)).findByIdAsDTO(classroomId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void getUpdateForm_whenStudentNoFoundClassroomServiceThrowsServiceException_thenProcessError()
        throws Exception {
        Long classroomId = 1L;

        String exceptionMessage = "Not found";
        when(classroomService.findByIdAsDTO(classroomId)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/classrooms/update/{id}", classroomId))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classroomService, times(1)).findByIdAsDTO(classroomId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_happyPath() throws Exception {
        Long classroomId = 1L;
        ClassroomDTO classroomDTO = new ClassroomDTO(1L, "Classroom A", new BuildingDTO(1L, "Building A", "Address A"));

        when(classroomService.save(classroomDTO)).thenReturn(classroomId);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classrooms/update/{id}", classroomId).flashAttr("classroomDTO", classroomDTO)
                    .with(csrf())).andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/classrooms/update/" + classroomId));

        verify(classroomService, times(1)).save(classroomDTO);
        verifyNoMoreInteractions(classroomService);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenBindingResultHasErrors_happyPath() throws Exception {
        Long classroomId = 1L;
        ClassroomDTO classroomDTO = new ClassroomDTO(1L, "", new BuildingDTO(1L, "Building A", "Address A"));

        when(classroomService.findByIdAsDTO(classroomId)).thenReturn(classroomDTO);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classrooms/update/{id}", classroomId).flashAttr("classroomDTO", classroomDTO)
                    .with(csrf())).andExpect(status().isOk()).andExpect(view().name("classroomsUpdateForm"))
            .andExpect(model().attributeExists("entity", "buildingDTOList")).andExpect(model().hasErrors());

        verify(classroomService, never()).save(any(ClassroomDTO.class));
        verify(classroomService, times(1)).findByIdAsDTO(classroomId);
        verify(buildingService, times(1)).findAllAsDTO();
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenServiceThrowsValidationException_thenProcessError() throws Exception {
        Long classroomId = 1L;
        ClassroomDTO classroomDTO = new ClassroomDTO(1L, "Classroom A", new BuildingDTO(1L, "Building A", "Address A"));

        String exceptionMessage = "Validation Error";
        when(classroomService.save(classroomDTO)).thenThrow(new ValidationException(exceptionMessage));

        mockMvc.perform(
            MockMvcRequestBuilders.post("/classrooms/update/{id}", classroomId).flashAttr("classroomDTO", classroomDTO)
                .with(csrf())).andExpect(status().is3xxRedirection());

        verify(classroomService, times(1)).save(classroomDTO);
        verifyNoMoreInteractions(classroomService);
        verifyNoInteractions(buildingService);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenServiceThrowsServiceException_thenProcessError() throws Exception {
        Long classroomId = 1L;
        ClassroomDTO classroomDTO = new ClassroomDTO(1L, "Classroom A", new BuildingDTO(1L, "Building A", "Address A"));

        String exceptionMessage = "Service Exception";
        when(classroomService.save(classroomDTO)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classrooms/update/{id}", classroomId).flashAttr("classroomDTO", classroomDTO)
                    .with(csrf())).andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classroomService, times(1)).save(classroomDTO);
        verifyNoMoreInteractions(classroomService);
        verifyNoInteractions(buildingService);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void getInsertForm_happyPath() throws Exception {
        List<BuildingDTO> buildingDTOList = new ArrayList<>();
        buildingDTOList.add(new BuildingDTO(1L, "Building A", "Address A"));
        buildingDTOList.add(new BuildingDTO(2L, "Building B", "Address B"));

        when(buildingService.findAllAsDTO()).thenReturn(buildingDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/classrooms/insert")).andExpect(status().isOk())
            .andExpect(model().attributeExists("buildingDTOList")).andExpect(view().name("classroomsInsertForm"))
            .andExpect(model().attribute("buildingDTOList", buildingDTOList));

        verify(buildingService, times(1)).findAllAsDTO();
    }

    @Test
    @WithMockUser(username = USERNAME)
    public void getInsertFrom_whenAccessDenied_processError() throws Exception {
        mockMvc.perform(get("/classrooms/insert")).andExpect(status().is2xxSuccessful())
            .andExpect(model().attributeExists("exceptionMessage")).andExpect(view().name("error"));
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_happyPath() throws Exception {
        Long classroomId = 1L;
        ClassroomDTO classroomDTO = new ClassroomDTO(1L, "Classroom A", new BuildingDTO(1L, "Building A", "Address A"));

        when(classroomService.save(classroomDTO)).thenReturn(classroomId);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classrooms/insert").flashAttr("classroomDTO", classroomDTO).with(csrf()))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/classrooms"));

        verify(classroomService, times(1)).save(classroomDTO);
        verifyNoMoreInteractions(classroomService);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenBindingResultHasErrors_happyPath() throws Exception {
        ClassroomDTO classroomDTO = new ClassroomDTO(1L, "", new BuildingDTO(1L, "Building A", "Address A"));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classrooms/insert").flashAttr("classroomDTO", classroomDTO).with(csrf()))
            .andExpect(status().isOk()).andExpect(view().name("classroomsInsertForm"))
            .andExpect(model().attributeExists("buildingDTOList")).andExpect(model().hasErrors());

        verify(classroomService, never()).save(any(ClassroomDTO.class));
        verify(buildingService, times(1)).findAllAsDTO();
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenServiceThrowsValidationException_thenProcessError() throws Exception {
        ClassroomDTO classroomDTO = new ClassroomDTO(1L, "Classroom A", new BuildingDTO(1L, "Building A", "Address A"));

        String exceptionMessage = "Validation Error";
        when(classroomService.save(classroomDTO)).thenThrow(new ValidationException(exceptionMessage));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classrooms/insert").flashAttr("classroomDTO", classroomDTO).with(csrf()))
            .andExpect(status().is3xxRedirection());

        verify(classroomService, times(1)).save(classroomDTO);
        verifyNoMoreInteractions(classroomService);
        verifyNoInteractions(buildingService);

    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenServiceThrowsServiceException_thenProcessError() throws Exception {
        ClassroomDTO classroomDTO = new ClassroomDTO(1L, "Classroom A", new BuildingDTO(1L, "Building A", "Address A"));

        String exceptionMessage = "Service Exception";
        when(classroomService.save(classroomDTO)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/classrooms/insert").flashAttr("classroomDTO", classroomDTO).with(csrf()))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(classroomService, times(1)).save(classroomDTO);
        verifyNoMoreInteractions(classroomService);
        verifyNoInteractions(buildingService);
    }
}
