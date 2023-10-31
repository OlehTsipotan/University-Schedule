package com.university.schedule.controller;

import com.university.schedule.config.WebTestConfig;
import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.BuildingService;
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

@WebMvcTest(BuildingRecordsController.class)
@Import(WebTestConfig.class)
@ActiveProfiles("test")
public class BuildingRecordsControllerTest {

    public static final String USERNAME = "testUsername";
    public static final String VIEW_AUTHORITY = "VIEW_BUILDINGS";
    public static final String EDIT_AUTHORITY = "EDIT_BUILDINGS";
    public static final String INSERT_AUTHORITY = "INSERT_BUILDINGS";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BuildingService buildingService;

    @Test
    @WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
    public void getAll_whenNoArgs_happyPath() throws Exception {
        List<BuildingDTO> buildingList = new ArrayList<>();
        buildingList.add(new BuildingDTO(1L, "Building A", "Address A"));
        buildingList.add(new BuildingDTO(2L, "Building B", "Address B"));

        when(buildingService.findAllAsDTO(any())).thenReturn(buildingList);

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings?offset=5")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("buildings"))
            .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
            .andExpect(model().attribute("entities", buildingList));

        verify(buildingService, times(1)).findAllAsDTO(any());
    }

    @ParameterizedTest
    @CsvSource({"5, 10"})
    @WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
    public void getAll_whenLimitAndOffsetArgs_happyPath(int limit, int offset) throws Exception {
        List<BuildingDTO> buildingList = new ArrayList<>();
        buildingList.add(new BuildingDTO(1L, "Building A", "Address A"));
        buildingList.add(new BuildingDTO(2L, "Building B", "Address B"));

        when(buildingService.findAllAsDTO(any())).thenReturn(buildingList);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/buildings?offset=%d&limit=%d", offset, limit)))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("buildings"))
            .andExpect(model().attributeExists("entities", "sortField", "sortDirection", "reverseSortDirection"))
            .andExpect(model().attribute("entities", buildingList));

        verify(buildingService, times(1)).findAllAsDTO(
            argThat(pageable -> pageable.getOffset() == offset && pageable.getPageSize() == limit));
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void delete_happyPath() throws Exception {
        Long buildingId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings/delete/{id}", buildingId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/buildings")); // Check if redirected back to /buildings

        verify(buildingService, times(1)).deleteById(buildingId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void delete_whenBuildingServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
        Long buildingId = 1L;
        String exceptionMessage = "Delete Error";

        doThrow(new DeletionFailedException(exceptionMessage)).when(buildingService).deleteById(buildingId);

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings/delete/{id}", buildingId))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(buildingService, times(1)).deleteById(buildingId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void getUpdateForm_happyPath() throws Exception {
        Long buildingId = 1L;
        BuildingDTO buildingDTO = new BuildingDTO(1L, "Building A", "Address A");

        when(buildingService.findByIdAsDTO(buildingId)).thenReturn(buildingDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings/update/{id}", buildingId)).andExpect(status().isOk())
            .andExpect(model().attributeExists("entity")).andExpect(view().name("buildingsUpdateForm"))
            .andExpect(model().attribute("entity", buildingDTO));

        verify(buildingService, times(1)).findByIdAsDTO(buildingId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void getUpdateForm_whenStudentNoFoundBuildingServiceThrowsServiceException_thenProcessError()
        throws Exception {
        Long buildingId = 1L;

        String exceptionMessage = "Not found";
        when(buildingService.findByIdAsDTO(buildingId)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings/update/{id}", buildingId))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(buildingService, times(1)).findByIdAsDTO(buildingId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenValidBuilding_thenRedirectSuccess() throws Exception {
        Long buildingId = 1L;
        BuildingDTO buildingDTO = BuildingDTO.builder().id(buildingId).name("Building A").address("Address A").build();

        mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId).with(csrf())
                .flashAttr("buildingDTO", buildingDTO)).andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/buildings/update/" + buildingId));

        verify(buildingService, times(1)).save(buildingDTO);
        verify(buildingService, times(0)).findByIdAsDTO(any());
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenNotValidBuildingByEmptyField_thenProcessForm() throws Exception {
        Long buildingId = 1L;
        BuildingDTO buildingDTO = BuildingDTO.builder().id(buildingId).name("").address("Address A").build();
        when(buildingService.findByIdAsDTO(buildingId)).thenReturn(buildingDTO);


        mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId).with(csrf())
                .flashAttr("buildingDTO", buildingDTO)).andExpect(status().is2xxSuccessful())
            .andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", buildingDTO))
            .andExpect(view().name("buildingsUpdateForm"));

        verify(buildingService, times(0)).save((BuildingDTO) any());
        verify(buildingService, times(1)).findByIdAsDTO(buildingId);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenBuildingServiceThrowValidationException_thenProcessForm() throws Exception {
        Long buildingId = 1L;
        BuildingDTO buildingDTO = BuildingDTO.builder().id(buildingId).name("Building A").address("Address A").build();

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(buildingService.save(buildingDTO)).thenThrow(validationException);

        mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId).with(csrf())
            .flashAttr("buildingDTO", buildingDTO)).andExpect(status().is3xxRedirection());

        verify(buildingService, times(1)).save(buildingDTO);
        verify(buildingService, times(0)).findByIdAsDTO(any());
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenBuildingServiceThrowServiceException_processError() throws Exception {
        Long buildingId = 1L;
        BuildingDTO buildingDTO = BuildingDTO.builder().id(buildingId).name("Building A").address("Address A").build();
        String exceptionMessage = "Service Exception";

        when(buildingService.save(buildingDTO)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(MockMvcRequestBuilders.post("/buildings/update/{id}", buildingId).with(csrf())
                .flashAttr("buildingDTO", buildingDTO)).andExpect(status().is2xxSuccessful())
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(buildingService, times(1)).save(buildingDTO);
        verify(buildingService, times(0)).findByIdAsDTO(any());
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void getInsertForm_happyPath() throws Exception {
        mockMvc.perform(get("/buildings/insert")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("buildingsInsertForm"));
    }

    @Test
    @WithMockUser(username = USERNAME)
    public void getInsertFrom_whenAccessDenied_processError() throws Exception {
        mockMvc.perform(get("/buildings/insert")).andExpect(status().is2xxSuccessful())
            .andExpect(model().attributeExists("exceptionMessage")).andExpect(view().name("error"));
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenValidBuilding_thenRedirectSuccess() throws Exception {
        Long buildingId = 0L;
        BuildingDTO buildingDTO = BuildingDTO.builder().name("Building A").address("Address A").build();

        when(buildingService.save(buildingDTO)).thenReturn(buildingId);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/buildings/insert").with(csrf()).flashAttr("buildingDTO", buildingDTO))
            .andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/buildings"))
            .andExpect(flash().attribute("insertedSuccessId", buildingId)); // First inserted elemen

        verify(buildingService, times(1)).save(buildingDTO);
        verifyNoMoreInteractions(buildingService);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenNotValidBuildingByEmptyField_thenProcessForm() throws Exception {
        BuildingDTO buildingDTO = BuildingDTO.builder().name("").address("Address A").build();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/buildings/insert").with(csrf()).flashAttr("buildingDTO", buildingDTO))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("buildingsInsertForm"));

        verifyNoInteractions(buildingService);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenBuildingServiceThrowValidationException_thenProcessForm() throws Exception {
        BuildingDTO buildingDTO = BuildingDTO.builder().name("Building A").address("Address A").build();

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(buildingService.save(buildingDTO)).thenThrow(validationException);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/buildings/insert").with(csrf()).flashAttr("buildingDTO", buildingDTO))
            .andExpect(status().is3xxRedirection());

        verify(buildingService, times(1)).save(buildingDTO);
        verifyNoMoreInteractions(buildingService);
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
    public void insert_whenBuildingServiceThrowServiceException_processError() throws Exception {
        BuildingDTO buildingDTO = BuildingDTO.builder().name("Building A").address("Address A").build();

        String exceptionMessage = "Service Exception";

        when(buildingService.save(buildingDTO)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/buildings/insert").with(csrf()).flashAttr("buildingDTO", buildingDTO))
            .andExpect(status().is2xxSuccessful()).andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(buildingService, times(1)).save(buildingDTO);
        verifyNoMoreInteractions(buildingService);
    }

}
