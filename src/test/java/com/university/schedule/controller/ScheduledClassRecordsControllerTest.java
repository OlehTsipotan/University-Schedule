package com.university.schedule.controller;

import com.university.schedule.config.WebTestConfig;
import com.university.schedule.dto.*;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.*;
import com.university.schedule.utility.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ScheduledClassRecordsController.class)
@Import(WebTestConfig.class)
@ActiveProfiles("test")
public class ScheduledClassRecordsControllerTest {

	private static final String USERNAME = "testUsername";
	private static final String VIEW_CLASSES = "VIEW_CLASSES";
	private static final String VIEW_SCHEDULE = "VIEW_SCHEDULE";
	private static final String EDIT_CLASSES = "EDIT_CLASSES";
	private static final String INSERT_CLASSES = "INSERT_CLASSES";
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ClassroomService classroomService;
	@MockBean
	private ClassTimeService classTimeService;
	@MockBean
	private ScheduledClassService scheduledClassService;
	@MockBean
	private CourseService courseService;
	@MockBean
	private TeacherService teacherService;
	@MockBean
	private ClassTypeService classTypeService;
	@MockBean
	private GroupService groupService;

	@Mock
	private DateUtils dateUtils;

	public ScheduledClassDTO getValidScheduledClassDTO() {
		return new ScheduledClassDTO(1L, new CourseDTO(1L, "Course A"),
				new TeacherDTO(1L, "teacher@example.com", "John", "Doe", new RoleDTO(), true, new ArrayList<>()),
				new ClassroomDTO(1L, "Classroom A", new BuildingDTO(1L, "Building A", "Address A")),
				new ClassTimeDTO(1L, 1, LocalTime.of(9, 0), 90), LocalDate.of(2023, 10, 17),
				new ClassTypeDTO(1L, "Lecture"), new ArrayList<>());
	}

	public List<ScheduledClassDTO> getValidScheduledClassDTOList() {
		return List.of(new ScheduledClassDTO(1L, new CourseDTO(1L, "Course A"),
						new TeacherDTO(1L, "teacher@example.com", "John", "Doe", new RoleDTO(), true, new ArrayList<>()),
						new ClassroomDTO(1L, "Classroom A", new BuildingDTO(1L, "Building A", "Address A")),
						new ClassTimeDTO(1L, 1, LocalTime.of(9, 0), 90), LocalDate.of(2023, 10, 17),
						new ClassTypeDTO(1L, "Lecture"), new ArrayList<>()),
				new ScheduledClassDTO(2L, new CourseDTO(2L, "Course B"),
						new TeacherDTO(2L, "teacher2@example.com", "Jane", "Doe", new RoleDTO(), true,
								new ArrayList<>()),
						new ClassroomDTO(2L, "Classroom B", new BuildingDTO(2L, "Building B", "Address B")),
						new ClassTimeDTO(2L, 2, LocalTime.of(10, 0), 60), LocalDate.of(2023, 10, 18),
						new ClassTypeDTO(2L, "Lab"), new ArrayList<>()));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = VIEW_CLASSES)
	public void getAll_whenNoArgs_happyPath() throws Exception {
		List<ScheduledClassDTO> scheduledClassDTOS = getValidScheduledClassDTOList();

		when(scheduledClassService.findAllAsDTO(any())).thenReturn(scheduledClassDTOS);

		mockMvc.perform(MockMvcRequestBuilders.get("/classes")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("classes")).andExpect(
						model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
								"reverseSortDirection")).andExpect(model().attribute("entities", scheduledClassDTOS));

		verify(scheduledClassService, times(1)).findAllAsDTO(any());
	}


	@ParameterizedTest
	@CsvSource({"5, 10"})
	@WithMockUser(username = USERNAME, authorities = VIEW_CLASSES)
	public void getAll_whenLimitAndOffsetArgs_happyPath(int limit, int offset) throws Exception {
		List<ScheduledClassDTO> scheduledClassDTOS = getValidScheduledClassDTOList();

		when(scheduledClassService.findAllAsDTO(any(Pageable.class))).thenReturn(scheduledClassDTOS);

		mockMvc.perform(MockMvcRequestBuilders.get(String.format("/classes?offset=%d&limit=%d", offset, limit)))
				.andExpect(status().is2xxSuccessful()).andExpect(view().name("classes")).andExpect(
						model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
								"reverseSortDirection"));

		verify(scheduledClassService, times(1)).findAllAsDTO(
				argThat(pageable -> pageable.getOffset() == offset && pageable.getPageSize() == limit));
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_CLASSES)
	public void delete_happyPath() throws Exception {
		Long classId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/classes/delete/{id}", classId))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/classes")); // Check if redirected back to /classes

		verify(scheduledClassService, times(1)).deleteById(classId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_CLASSES)
	public void delete_whenScheduledClassServiceThrowsDeletionFailedException_thenProcessError() throws Exception {
		Long scheduledClassId = 1L;
		String exceptionMessage = "Delete Error";

		doThrow(new DeletionFailedException(exceptionMessage)).when(scheduledClassService).deleteById(scheduledClassId);

		mockMvc.perform(MockMvcRequestBuilders.get("/classes/delete/{id}", scheduledClassId))
				.andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(scheduledClassService, times(1)).deleteById(scheduledClassId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_CLASSES)
	public void getUpdateForm_happyPath() throws Exception {
		Long scheduledClassId = 1L;
		ScheduledClassDTO scheduledClassDTO = getValidScheduledClassDTO();

		when(scheduledClassService.findByIdAsDTO(scheduledClassId)).thenReturn(scheduledClassDTO);

		mockMvc.perform(MockMvcRequestBuilders.get("/classes/update/{id}", scheduledClassId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity")).andExpect(view().name("classesUpdateForm"))
				.andExpect(model().attribute("entity", scheduledClassDTO));

		verify(scheduledClassService, times(1)).findByIdAsDTO(scheduledClassId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_CLASSES)
	public void update_whenValidScheduledClass_thenRedirectSuccess() throws Exception {
		ScheduledClassDTO scheduledClassDTO = getValidScheduledClassDTO();
		Long scheduledClassId = scheduledClassDTO.getId();

		when(scheduledClassService.save(any(ScheduledClassDTO.class))).thenReturn(scheduledClassId);

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/update/{id}", scheduledClassId)
						.flashAttr("scheduledClassDTO", scheduledClassDTO)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/classes/update/" + scheduledClassId));

		verify(scheduledClassService, times(1)).save(any(ScheduledClassDTO.class));
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_CLASSES)
	public void update_whenNotValidScheduledClassDTOByNullField_thenProcessForm() throws Exception {
		Long classId = 1L;
		ScheduledClassDTO scheduledClassDTO = getValidScheduledClassDTO();
		scheduledClassDTO.setClassroomDTO(null);

		when(scheduledClassService.findByIdAsDTO(classId)).thenReturn(scheduledClassDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/update/{id}", classId).with(csrf())
						.flashAttr("scheduledClassDTO", scheduledClassDTO)).andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", scheduledClassDTO))
				.andExpect(view().name("classesUpdateForm"));

		verify(scheduledClassService, times(0)).save((ScheduledClassDTO) any());
		verify(scheduledClassService, times(1)).findByIdAsDTO(classId);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_CLASSES)
	public void update_whenScheduledClassServiceThrowsValidationException_thenProcessForm() throws Exception {
		ScheduledClassDTO scheduledClassDTO = getValidScheduledClassDTO();
		Long scheduledClassId = scheduledClassDTO.getId();

		ValidationException validationException = new ValidationException("testException", List.of("myError"));
		doThrow(validationException).when(scheduledClassService).save(any(ScheduledClassDTO.class));

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/update/{id}", scheduledClassId).with(csrf())
				.flashAttr("scheduledClassDTO", scheduledClassDTO)).andExpect(status().is3xxRedirection());

		verify(scheduledClassService, times(1)).save(any(ScheduledClassDTO.class));
		verifyNoMoreInteractions(scheduledClassService);
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = EDIT_CLASSES)
	public void update_whenScheduledClassServiceThrowsServiceException_thenProcessError() throws Exception {
		ScheduledClassDTO scheduledClassDTO =
				getValidScheduledClassDTO(); // Assuming you have a method to generate a valid ScheduledClassDTO
		Long scheduledClassId = scheduledClassDTO.getId();
		String exceptionMessage = "Service Exception";

		when(scheduledClassService.save(any(ScheduledClassDTO.class))).thenThrow(
				new ServiceException(exceptionMessage));

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/update/{id}", scheduledClassId).with(csrf())
						.flashAttr("scheduledClassDTO", scheduledClassDTO)).andExpect(status().isOk())
				.andExpect(view().name("error")).andExpect(model().attribute("exceptionMessage", exceptionMessage));

		verify(scheduledClassService, times(1)).save(any(ScheduledClassDTO.class));
		verify(scheduledClassService, times(0)).findByIdAsDTO(any());
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_CLASSES)
	public void getInsertForm_happyPath() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/classes/insert")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("classesInsertForm")).andExpect(
						model().attributeExists("courseDTOList", "teacherDTOList", "classroomDTOList", "classTimeDTOList",
								"classTypeDTOList", "groupDTOList"));
	}

	@Test
	@WithMockUser(username = USERNAME)
	public void getInsertFrom_whenAccessDenied_processError() throws Exception {
		mockMvc.perform(get("/classes/insert")).andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeExists("exceptionMessage")).andExpect(view().name("error"));
	}


	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_CLASSES)
	public void insert_whenValidScheduledClass_thenRedirectSuccess() throws Exception {
		ScheduledClassDTO scheduledClassDTO = getValidScheduledClassDTO();

		when(scheduledClassService.save(scheduledClassDTO)).thenReturn(1L);

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/insert").with(csrf())
						.flashAttr("scheduledClassDTO", scheduledClassDTO)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/classes"));

		verify(scheduledClassService, times(1)).save(scheduledClassDTO);
		verifyNoMoreInteractions(scheduledClassService);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_CLASSES)
	public void insert_whenNotValidScheduledClassDTOByNullField_thenProcessForm() throws Exception {
		Long classId = 1L;
		ScheduledClassDTO scheduledClassDTO = getValidScheduledClassDTO();
		scheduledClassDTO.setClassroomDTO(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/insert", classId).with(csrf())
						.flashAttr("scheduledClassDTO", scheduledClassDTO)).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("classesInsertForm"));

		verify(scheduledClassService, times(0)).save((ScheduledClassDTO) any());
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = VIEW_SCHEDULE)
	public void getSchedule_happyPath() throws Exception {
		Long scheduledClassId = 1L;
		List<ScheduledClassDTO> scheduledClassDTOS = getValidScheduledClassDTOList();

		ScheduleFilterItem scheduleFilterItem = ScheduleFilterItem.builder().email(USERNAME).build();

		when(scheduledClassService.findAllAsDTOByScheduleFilterItem(scheduleFilterItem)).thenReturn(scheduledClassDTOS);

		try (MockedStatic<DateUtils> utilities = Mockito.mockStatic(DateUtils.class)) {
			utilities.when(() -> DateUtils.getDatesBetween(any(), any()))
					.thenReturn(List.of(LocalDate.of(2023, 5, 1), LocalDate.of(2023, 5, 2)));

			mockMvc.perform(MockMvcRequestBuilders.get("/schedule", scheduledClassId))
					.andExpect(model().attribute("scheduledClassDTOS", scheduledClassDTOS))
					.andExpect(status().is2xxSuccessful()).andExpect(view().name("schedule"));

		}

		verify(scheduledClassService, times(1)).findAllAsDTOByScheduleFilterItem(scheduleFilterItem);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = VIEW_SCHEDULE)
	public void getScheduleFiltered_happyPath() throws Exception {
		Long scheduledClassId = 1L;
		List<ScheduledClassDTO> scheduledClassDTOS = getValidScheduledClassDTOList();

		ScheduleFilterItem scheduleFilterItem =
				ScheduleFilterItem.builder().email(USERNAME).startDate(LocalDate.of(2023, 5, 5))
						.endDate(LocalDate.of(2023, 5, 10)).build();

		when(scheduledClassService.findAllAsDTOByScheduleFilterItem(scheduleFilterItem)).thenReturn(scheduledClassDTOS);

		mockMvc.perform(MockMvcRequestBuilders.post("/schedule", scheduledClassId)
						.flashAttr("scheduleFilterItem", scheduleFilterItem)).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("schedule"))
				.andExpect(model().attribute("scheduledClassDTOS", scheduledClassDTOS))
				.andExpect(model().attribute("filtered", true));

		verify(scheduledClassService, times(1)).findAllAsDTOByScheduleFilterItem(scheduleFilterItem);
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_CLASSES)
	public void insert_whenScheduledClassServiceThrowsValidationException_thenProcessForm() throws Exception {
		ScheduledClassDTO scheduledClassDTO = getValidScheduledClassDTO();

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(scheduledClassService.save(scheduledClassDTO)).thenThrow(validationException);

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/insert").with(csrf())
				.flashAttr("scheduledClassDTO", scheduledClassDTO)).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(username = USERNAME, authorities = INSERT_CLASSES)
	public void insert_whenScheduledClassServiceThrowsServiceException_thenProcessError() throws Exception {
		ScheduledClassDTO scheduledClassDTO = getValidScheduledClassDTO();

		String exceptionMessage = "Service Exception";

		when(scheduledClassService.save(scheduledClassDTO)).thenThrow(new ServiceException(exceptionMessage));

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/insert").with(csrf())
						.flashAttr("scheduledClassDTO", scheduledClassDTO)).andExpect(status().isOk())
				.andExpect(model().attribute("exceptionMessage", exceptionMessage));
	}

}
