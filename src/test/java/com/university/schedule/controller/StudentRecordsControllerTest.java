package com.university.schedule.controller;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.StudentDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.GroupService;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentRecordsController.class)
public class StudentRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StudentService studentService;

	@MockBean
	private GroupService groupService;

	@MockBean
	private RoleService roleService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_STUDENTS"})
	public void getAll_processPage() throws Exception {
		GroupDTO groupDTO = new GroupDTO(1L, "groupDTOName", null, null);
		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		// Create a list of StudentDTOs for testing
		List<StudentDTO> studentDTOs = new ArrayList<>();
		studentDTOs.add(new StudentDTO(1L, "email 1", "firstName 1", "lastName 1", roleDTO, true, groupDTO));

		// Mock the behavior of studentDTOService
		when(studentService.findAllAsDTO(any(Pageable.class))).thenReturn(studentDTOs);

		// Perform a GET request to /students and verify the result
		mockMvc.perform(MockMvcRequestBuilders.get("/students")).andExpect(status().isOk())
				.andExpect(view().name("students")).andExpect(
						model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
								"reverseSortDirection")).andExpect(model().attribute("entities", studentDTOs));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_STUDENTS"})
	public void delete() throws Exception {
		Long studentId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/students/delete/{id}", studentId))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/students"));

		verify(studentService, times(1)).deleteById(studentId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_STUDENTS"})
	public void getUpdateForm() throws Exception {
		Long studentId = 1L;

		GroupDTO groupDTO = new GroupDTO(1L, "groupDTOName", null, null);
		List<GroupDTO> groupDTOS = List.of(groupDTO);

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		// Create a list of StudentDTOs for testing
		StudentDTO studentDTO =
				new StudentDTO(studentId, "email 1", "firstName 1", "lastName 1", roleDTO, true, groupDTO);

		when(studentService.findByIdAsDTO(studentId)).thenReturn(studentDTO);
		when(groupService.findAllAsDTO()).thenReturn(groupDTOS);
		when(roleService.findAllAsDTO()).thenReturn(roleDTOS);

		mockMvc.perform(MockMvcRequestBuilders.get("/students/update/{id}", studentId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity", "groupDTOList", "roleDTOList"))
				.andExpect(view().name("studentsUpdateForm")).andExpect(model().attribute("entity", studentDTO))
				.andExpect(model().attribute("groupDTOList", groupDTOS))
				.andExpect(model().attribute("roleDTOList", roleDTOS));

		verify(studentService, times(1)).findByIdAsDTO(studentId);
		verify(groupService, times(1)).findAllAsDTO();
		verify(roleService, times(1)).findAllAsDTO();
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_STUDENTS"})
	public void update_whenNotValidStudentUpdateDTO_emptyField_thenProcessForm() throws Exception {
		Long studentId = 1L;

		GroupDTO groupDTO = new GroupDTO(1L, "groupDTOName", null, null);
		List<GroupDTO> groupDTOS = List.of(groupDTO);

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		// Create a list of StudentDTOs for testing
		StudentDTO studentDTO = new StudentDTO(studentId, "email 1", "firstName 1", "", roleDTO, true, groupDTO);

		when(studentService.findByIdAsDTO(studentId)).thenReturn(studentDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/students/update/{id}", studentId)
						.param("isEnable", "false") // Add any other request parameters as needed
						.with(csrf()).flashAttr("studentDTO", studentDTO).flashAttr("groupDTOList", groupDTOS))
				.andExpect(status().isOk()).andExpect(model().attributeExists("entity"))
				.andExpect(model().attribute("entity", studentDTO)).andExpect(view().name("studentsUpdateForm"));

		verify(studentService, times(0)).save(studentDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_STUDENTS"})
	public void update_whenStudentUpdateDTOServiceThrowValidationException_thenProcessForm() throws Exception {
		Long studentId = 1L;

		GroupDTO groupDTO = new GroupDTO(1L, "groupDTOName", null, null);
		List<GroupDTO> groupDTOS = List.of(groupDTO);

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		// Create a list of StudentDTOs for testing
		StudentDTO studentDTO =
				new StudentDTO(studentId, "email 1", "firstName 1", "lastName 1", roleDTO, true, groupDTO);

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(studentService.save((StudentDTO) any())).thenThrow(validationException);
		when(studentService.findByIdAsDTO(studentId)).thenReturn(studentDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/students/update/{id}", studentId)
				.param("isEnable", "false") // Add any other request parameters as needed
				.with(csrf()).flashAttr("studentDTO", studentDTO)).andExpect(status().is3xxRedirection());

		verify(studentService, times(1)).save(studentDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_STUDENTS"})
	public void update_whenStudentUpdateDTOServiceThrowServiceException_thenProcessForm() throws Exception {
		Long studentId = 1L;

		GroupDTO groupDTO = new GroupDTO(1L, "groupDTOName", null, null);
		List<GroupDTO> groupDTOS = List.of(groupDTO);

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		List<RoleDTO> roleDTOS = List.of(roleDTO);
		// Create a list of StudentDTOs for testing
		StudentDTO studentDTO =
				new StudentDTO(studentId, "email 1", "firstName 1", "lastName 1", roleDTO, true, groupDTO);

		ServiceException serviceException = new ServiceException("testException");

		when(studentService.save((StudentDTO) any())).thenThrow(serviceException);
		when(studentService.findByIdAsDTO(studentId)).thenReturn(studentDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/students/update/{id}", studentId)
				.param("isEnable", "false") // Add any other request parameters as needed
				.with(csrf()).flashAttr("studentDTO", studentDTO)).andExpect(status().is3xxRedirection());

		verify(studentService, times(1)).save(studentDTO);
	}
}
