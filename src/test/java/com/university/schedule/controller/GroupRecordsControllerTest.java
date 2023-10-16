package com.university.schedule.controller;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.CourseService;
import com.university.schedule.service.DisciplineService;
import com.university.schedule.service.GroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupRecordsController.class)
@ActiveProfiles("test")
public class GroupRecordsControllerTest {

	private static final String USERNAME = "testUsername";
	private static final String VIEW_AUTHORITY = "VIEW_GROUPS";
	private static final String EDIT_AUTHORITY = "EDIT_GROUPS";
	private static final String INSERT_AUTHORITY = "INSERT_GROUPS";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GroupService groupService;

	@MockBean
	private DisciplineService disciplineService;

	@MockBean
	private CourseService courseService;

	@Test
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenNoArgs_happyPath() throws Exception {
		// Arrange
		List<GroupDTO> groupDTOList = new ArrayList<>();
		groupDTOList.add(new GroupDTO(1L, "Group A", new DisciplineDTO(1L, "Math"), new ArrayList<>()));
		groupDTOList.add(new GroupDTO(2L, "Group B", new DisciplineDTO(2L, "Science"), new ArrayList<>()));

		when(groupService.findAllAsDTO(any())).thenReturn(groupDTOList);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/groups"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("groups"))
				.andExpect(model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", groupDTOList));

		verify(groupService, times(1)).findAllAsDTO(any());
	}



	@ParameterizedTest
	@CsvSource({"5, 10"})
	@WithMockUser(username = USERNAME, authorities = VIEW_AUTHORITY)
	public void getAll_whenLimitAndOffsetArgs_happyPath(int limit, int offset) throws Exception {
		// Arrange
		List<GroupDTO> groupDTOList = new ArrayList<>();
		groupDTOList.add(new GroupDTO(1L, "Group A", new DisciplineDTO(1L, "Math"), new ArrayList<>()));
		groupDTOList.add(new GroupDTO(2L, "Group B", new DisciplineDTO(2L, "Science"), new ArrayList<>()));

		when(groupService.findAllAsDTO(any())).thenReturn(groupDTOList);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/groups")
						.param("limit", String.valueOf(limit))
						.param("offset", String.valueOf(offset)))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("groups"))
				.andExpect(model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection", "reverseSortDirection"))
				.andExpect(model().attribute("entities", groupDTOList));

		verify(groupService, times(1)).findAllAsDTO(
				argThat(pageable -> pageable.getOffset() == offset && pageable.getPageSize() == limit));
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_happyPath() throws Exception {
		// Arrange
		Long groupId = 1L;

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/groups/delete/{id}", groupId))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/groups"));

		verify(groupService, times(1)).deleteById(groupId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void delete_whenGroupServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
		// Arrange
		Long groupId = 1L;
		String exceptionMessage = "Deletion Failed";

		doThrow(new DeletionFailedException(exceptionMessage)).when(groupService).deleteById(groupId);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/groups/delete/{id}", groupId))
				.andExpect(status().isOk())
				.andExpect(view().name("error"))
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(groupService, times(1)).deleteById(groupId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_happyPath() throws Exception {
		// Arrange
		Long groupId = 1L;
		GroupDTO groupDTO = new GroupDTO(groupId, "Group A", new DisciplineDTO(), new ArrayList<>());

		when(groupService.findByIdAsDTO(groupId)).thenReturn(groupDTO);
		List<DisciplineDTO> disciplines = Arrays.asList(new DisciplineDTO(), new DisciplineDTO());

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/groups/update/{id}", groupId))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("entity", "disciplineDTOList", "courseDTOList"))
				.andExpect(view().name("groupsUpdateForm"))
				.andExpect(model().attribute("entity", groupDTO));

		verify(groupService, times(1)).findByIdAsDTO(groupId);
		verify(disciplineService, times(1)).findAllAsDTO();
		verify(courseService, times(1)).findAllAsDTO();
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void getUpdateForm_whenGroupServiceThrowsServiceException_thenProcessError() throws Exception {
		// Arrange
		Long groupId = 1L;
		String exceptionMessage = "Service Exception";

		when(groupService.findByIdAsDTO(groupId)).thenThrow(new ServiceException(exceptionMessage));

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/groups/update/{id}", groupId))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("error"))
				.andExpect(model().attributeExists("exceptionMessage"))
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(groupService, times(1)).findByIdAsDTO(groupId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenValidGroup_thenRedirectSuccess() throws Exception {
		// Arrange
		Long groupId = 1L;
		GroupDTO validGroupDTO = new GroupDTO(groupId, "Valid Group", new DisciplineDTO(), new ArrayList<>());

		// Mock the behavior of groupService.save(groupDTO)
		when(groupService.save(any(GroupDTO.class))).thenReturn(groupId);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/groups/update/{id}", groupId)
						.with(csrf())
						.flashAttr("groupDTO", validGroupDTO))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/groups/update/" + groupId));

		// Verify that groupService.save(groupDTO) was called with the validGroupDTO
		verify(groupService, times(1)).save(validGroupDTO);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenNotValidGroupByEmptyField_thenProcessForm() throws Exception {
		// Arrange
		Long groupId = 1L;
		GroupDTO invalidGroupDTO = new GroupDTO(groupId, "", new DisciplineDTO(), new ArrayList<>());
		when(groupService.findByIdAsDTO(groupId)).thenReturn(invalidGroupDTO);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/groups/update/{id}", groupId)
						.with(csrf())
						.flashAttr("groupDTO", invalidGroupDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("entity"))
				.andExpect(model().attribute("entity", invalidGroupDTO))
				.andExpect(view().name("groupsUpdateForm"));

		// Verify that groupService.save(groupDTO) was not called
		verify(groupService, times(0)).save(invalidGroupDTO);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenGroupServiceThrowValidationException_thenProcessForm() throws Exception {
		// Arrange
		Long groupId = 1L;
		GroupDTO groupDTO = new GroupDTO(groupId, "Group A", new DisciplineDTO(), new ArrayList<>());

		ValidationException validationException = new ValidationException("testException", List.of("myError"));
		when(groupService.save(groupDTO)).thenThrow(validationException);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/groups/update/{id}", groupId)
						.with(csrf())
						.flashAttr("groupDTO", groupDTO))
				.andExpect(status().is3xxRedirection());

		// Verify that groupService.save(groupDTO) was called
		verify(groupService, times(1)).save(groupDTO);
		// Verify that groupService.findByIdAsDTO(groupId) was not called
		verify(groupService, times(0)).findByIdAsDTO(groupId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_AUTHORITY)
	public void update_whenGroupServiceThrowServiceException_processError() throws Exception {
		// Arrange
		Long groupId = 1L;
		GroupDTO groupDTO = new GroupDTO(groupId, "Group A", new DisciplineDTO(), new ArrayList<>());

		String exceptionMessage = "Service Exception";
		when(groupService.save(groupDTO)).thenThrow(new ServiceException(exceptionMessage));

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/groups/update/{id}", groupId)
						.with(csrf())
						.flashAttr("groupDTO", groupDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("exceptionMessage"))
				.andExpect(view().name("error"))
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		// Verify that groupService.save(groupDTO) was called
		verify(groupService, times(1)).save(groupDTO);
		// Verify that groupService.findByIdAsDTO(groupId) was not called
		verify(groupService, times(0)).findByIdAsDTO(groupId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void getInsertForm_happyPath() throws Exception {
		// Arrange

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/groups/insert"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(view().name("groupsInsertForm"));

		// No need for verification in this case
	}

	@Test
	@WithMockUser(username = USERNAME)
	public void getInsertFrom_whenAccessDenied_processError() throws Exception {
		// Arrange

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/groups/insert"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("exceptionMessage"))
				.andExpect(view().name("error"));

		// No need for verification in this case
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenValidGroup_thenRedirectSuccess() throws Exception {
		// Arrange
		GroupDTO validGroupDTO = new GroupDTO(null, "Valid Group", new DisciplineDTO(), new ArrayList<>());
		Long insertedGroupId = 1L;
		when(groupService.save(validGroupDTO)).thenReturn(insertedGroupId);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/groups/insert").with(csrf())
						.flashAttr("groupDTO", validGroupDTO))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/groups"))
				.andExpect(flash().attribute("insertedSuccessId", insertedGroupId));

		verify(groupService, times(1)).save(validGroupDTO);
		verifyNoMoreInteractions(groupService);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenNotValidGroupByEmptyField_thenProcessForm() throws Exception {
		// Arrange
		Long groupId = 1L;
		GroupDTO invalidGroupDTO = new GroupDTO(groupId, "", new DisciplineDTO(), new ArrayList<>());

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/groups/insert")
						.with(csrf())
						.flashAttr("groupDTO", invalidGroupDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("disciplineDTOList", "courseDTOList"))
				.andExpect(view().name("groupsInsertForm"));

		// Verify that groupService.save(groupDTO) was not called
		verify(groupService, times(0)).save(invalidGroupDTO);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenGroupServiceThrowValidationException_thenProcessForm() throws Exception {
		// Arrange
		Long groupId = 1L;
		GroupDTO invalidGroupDTO = new GroupDTO(groupId, "Group A", new DisciplineDTO(), new ArrayList<>());

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(groupService.save(invalidGroupDTO)).thenThrow(validationException);

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/groups/insert")
						.with(csrf())
						.flashAttr("groupDTO", invalidGroupDTO))
				.andExpect(status().is3xxRedirection());

		// Verify that groupService.save(groupDTO) was called once
		verify(groupService, times(1)).save(invalidGroupDTO);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_AUTHORITY)
	public void insert_whenGroupServiceThrowServiceException_processError() throws Exception {
		// Arrange
		Long groupId = 1L;
		GroupDTO invalidGroupDTO = new GroupDTO(groupId, "Group A", new DisciplineDTO(), new ArrayList<>());

		String exceptionMessage = "Service Exception";

		when(groupService.save(invalidGroupDTO)).thenThrow(new ServiceException(exceptionMessage));

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.post("/groups/insert")
						.with(csrf())
						.flashAttr("groupDTO", invalidGroupDTO))
				.andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		// Verify that groupService.save(groupDTO) was called once
		verify(groupService, times(1)).save(invalidGroupDTO);
	}

}
