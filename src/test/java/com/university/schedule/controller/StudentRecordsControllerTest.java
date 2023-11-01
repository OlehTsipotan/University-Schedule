package com.university.schedule.controller;

import com.university.schedule.config.WebTestConfig;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.StudentDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.GroupService;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.StudentService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentRecordsController.class)
@Import(WebTestConfig.class)
@ActiveProfiles("test")
public class StudentRecordsControllerTest {

    public static final String USERNAME = "testUsername";
    public static final String VIEW_AUTHORITY = "VIEW_STUDENTS";
    public static final String EDIT_AUTHORITY = "EDIT_STUDENTS";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentService studentService;
    @MockBean
    private GroupService groupService;
    @MockBean
    private RoleService roleService;

    @Test
    @WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
    public void getAll_whenNoArgs_happyPath() throws Exception {
        List<StudentDTO> studentDTOList = new ArrayList<>();
        studentDTOList.add(
            new StudentDTO(1L, "student1@example.com", "John", "Doe", new RoleDTO(), true, new GroupDTO()));
        studentDTOList.add(
            new StudentDTO(2L, "student2@example.com", "Jane", "Doe", new RoleDTO(), true, new GroupDTO()));

        when(studentService.findAllAsDTO(anyString(), any())).thenReturn(studentDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/students?offset=5")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("students")).andExpect(
                model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
                    "reverseSortDirection")).andExpect(model().attribute("entities", studentDTOList));

        verify(studentService, times(1)).findAllAsDTO(assertArg(passedString -> passedString.equals(USERNAME)), any());
    }


    @ParameterizedTest
    @CsvSource({"5, 10"})
    @WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
    public void getAll_whenLimitAndOffsetArgs_happyPath(int limit, int offset) throws Exception {
        List<StudentDTO> studentDTOList = new ArrayList<>();
        studentDTOList.add(
            new StudentDTO(1L, "student1@example.com", "John", "Doe", new RoleDTO(), true, new GroupDTO()));
        studentDTOList.add(
            new StudentDTO(2L, "student2@example.com", "Jane", "Doe", new RoleDTO(), true, new GroupDTO()));

        when(studentService.findAllAsDTO(anyString(), any())).thenReturn(studentDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/students?offset=%d&limit=%d", offset, limit)))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("students")).andExpect(
                model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
                    "reverseSortDirection")).andExpect(model().attribute("entities", studentDTOList));

        verify(studentService, times(1)).findAllAsDTO(assertArg(passedString -> passedString.equals(USERNAME)), any());
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void delete_happyPath() throws Exception {
        Long studentId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/students/delete/{id}", studentId))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/students"));

        verify(studentService, times(1)).deleteById(studentId);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void delete_whenStudentServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
        Long studentId = 1L;
        String exceptionMessage = "Delete Error";

        doThrow(new DeletionFailedException(exceptionMessage)).when(studentService).deleteById(studentId);

        mockMvc.perform(MockMvcRequestBuilders.get("/students/delete/{id}", studentId))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(studentService, times(1)).deleteById(studentId);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void getUpdateForm_happyPath() throws Exception {
        Long studentId = 1L;
        StudentDTO studentDTO =
            new StudentDTO(1L, "student1@example.com", "John", "Doe", new RoleDTO(), true, new GroupDTO());

        when(studentService.findByIdAsDTO(studentId)).thenReturn(studentDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/students/update/{id}", studentId)).andExpect(status().isOk())
            .andExpect(model().attributeExists("entity")).andExpect(view().name("studentsUpdateForm"))
            .andExpect(model().attribute("entity", studentDTO));

        verify(studentService, times(1)).findByIdAsDTO(studentId);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void getUpdateForm_whenStudentNotFoundStudentServiceThrowsServiceException_thenProcessError()
        throws Exception {
        Long studentId = 1L;
        String exceptionMessage = "Not found";

        when(studentService.findByIdAsDTO(studentId)).thenThrow(new ServiceException(exceptionMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/students/update/{id}", studentId))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(studentService, times(1)).findByIdAsDTO(studentId);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenValidStudent_thenRedirectSuccess() throws Exception {
        Long studentId = 1L;
        StudentDTO studentDTO =
            new StudentDTO(studentId, "student1@example.com", "John", "Doe", new RoleDTO(), true, new GroupDTO());

        mockMvc.perform(MockMvcRequestBuilders.post("/students/update/{id}", studentId).with(csrf())
                .flashAttr("studentDTO", studentDTO)).andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/students/update/" + studentId));

        verify(studentService, times(1)).update(studentDTO);
        verify(studentService, times(0)).findByIdAsDTO(anyLong());
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenNotValidStudentByEmptyField_thenProcessForm() throws Exception {
        Long studentId = 1L;
        StudentDTO studentDTO =
            new StudentDTO(studentId, "student1@example.com", "", "Doe", new RoleDTO(), true, new GroupDTO());

        when(studentService.findByIdAsDTO(studentId)).thenReturn(studentDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/students/update/{id}", studentId).with(csrf())
                .flashAttr("studentDTO", studentDTO)).andExpect(status().is2xxSuccessful())
            .andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", studentDTO))
            .andExpect(view().name("studentsUpdateForm"));

        verify(studentService, times(0)).update(any());
        verify(studentService, times(1)).findByIdAsDTO(studentId);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenStudentServiceThrowValidationException_thenProcessForm() throws Exception {
        Long studentId = 1L;
        StudentDTO studentDTO =
            new StudentDTO(studentId, "student1@example.com", "John", "Doe", new RoleDTO(), true, new GroupDTO());

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        when(studentService.update(studentDTO)).thenThrow(validationException);
        when(studentService.findByIdAsDTO(studentId)).thenReturn(studentDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/students/update/{id}", studentId).with(csrf())
            .flashAttr("studentDTO", studentDTO)).andExpect(status().is3xxRedirection());

        verify(studentService, times(1)).update(studentDTO);
        verifyNoMoreInteractions(studentService);
    }


    @Test
    @WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
    public void update_whenStudentServiceThrowServiceException_thenProcessError() throws Exception {
        Long studentId = 1L;
        StudentDTO studentDTO =
            new StudentDTO(studentId, "student1@example.com", "John", "Doe", new RoleDTO(), true, new GroupDTO());

        String exceptionMessage = "Service Exception";
        ServiceException serviceException = new ServiceException(exceptionMessage);

        when(studentService.update(studentDTO)).thenThrow(serviceException);
        when(studentService.findByIdAsDTO(studentId)).thenReturn(studentDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/students/update/{id}", studentId).with(csrf())
                .flashAttr("studentDTO", studentDTO)).andExpect(status().is2xxSuccessful())
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(studentService, times(1)).update(studentDTO);
        verifyNoMoreInteractions(studentService);
    }

}
