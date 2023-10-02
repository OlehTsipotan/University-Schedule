package com.university.schedule.controller;

import com.university.schedule.dto.*;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduledClassRecordsController.class)
public class ScheduledClassRecordsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ClassroomService classroomService;

	@MockBean
	private ScheduledClassService scheduledClassService;

	@MockBean
	private CourseService courseService;

	@MockBean
	private TeacherService teacherService;

	@MockBean
	private ClassTimeService classTimeService;

	@MockBean
	private ClassTypeService classTypeService;

	@MockBean
	private GroupService groupService;

	@Test
	@WithMockUser(username = "username", authorities = {"VIEW_CLASSES"})
	public void getAll_processPage() throws Exception {
		CourseDTO courseDTO = new CourseDTO(1L, "CourseDTOName");
		List<CourseDTO> courseDTOS = List.of(courseDTO);

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		TeacherDTO teacherDTO =
				new TeacherDTO(1L, "teacher@email.com", "teacherFirstName", "teacherLastName", roleDTO, true,
						courseDTOS);

		DisciplineDTO disciplineDTO = new DisciplineDTO(1L, "disciplineDTOName");
		GroupDTO groupDTO = new GroupDTO(1L, "groupDTOName", disciplineDTO, courseDTOS);
		List<GroupDTO> groupDTOS = List.of(groupDTO);
		LocalDate localDate = LocalDate.of(2000, 1, 1);
		ClassTimeDTO classTimeDTO = new ClassTimeDTO(1L, 1, LocalTime.of(8, 30), 95);
		ClassTypeDTO classTypeDTO = new ClassTypeDTO(1L, "classTypeDTO");
		BuildingDTO buildingDTO = new BuildingDTO(1L, "buildingDTOName", "buildingDTOAddress");
		ClassroomDTO classroomDTO = new ClassroomDTO(1L, "classroomDTOName", buildingDTO);

		// Create a list of ScheduledClassDTOs for testing
		List<ScheduledClassDTO> scheduledClassDTOs = new ArrayList<>();
		scheduledClassDTOs.add(
				ScheduledClassDTO.builder().id(1L).courseDTO(courseDTO).teacherDTO(teacherDTO).groupDTOS(groupDTOS)
						.date(localDate).classTimeDTO(classTimeDTO).classTypeDTO(classTypeDTO)
						.classroomDTO(classroomDTO).build());

		// Mock the behavior of scheduledClassDTOService
		when(scheduledClassService.findAllAsDTO(any(Pageable.class))).thenReturn(scheduledClassDTOs);

		// Perform a GET request to /classes and verify the result
		mockMvc.perform(MockMvcRequestBuilders.get("/classes")).andExpect(status().isOk())
				.andExpect(view().name("classes")).andExpect(
						model().attributeExists("entities", "currentLimit", "currentOffset", "sortField", "sortDirection",
								"reverseSortDirection")).andExpect(model().attribute("entities", scheduledClassDTOs));
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSES"})
	public void delete() throws Exception {
		Long scheduledClassId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/classes/delete/{id}", scheduledClassId))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/classes"));

		verify(scheduledClassService, times(1)).deleteById(scheduledClassId);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSES"})
	public void getUpdateForm() throws Exception {
		Long scheduledClassId = 1L;

		CourseDTO courseDTO = new CourseDTO(1L, "CourseDTOName");
		List<CourseDTO> courseDTOS = List.of(courseDTO);

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		TeacherDTO teacherDTO =
				new TeacherDTO(1L, "teacher@email.com", "teacherFirstName", "teacherLastName", roleDTO, true,
						courseDTOS);
		List<TeacherDTO> teacherDTOS = List.of(teacherDTO);

		DisciplineDTO disciplineDTO = new DisciplineDTO(1L, "disciplineDTOName");
		GroupDTO groupDTO = new GroupDTO(1L, "groupDTOName", disciplineDTO, courseDTOS);
		List<GroupDTO> groupDTOS = List.of(groupDTO);

		LocalDate localDate = LocalDate.of(2000, 1, 1);

		ClassTimeDTO classTimeDTO = new ClassTimeDTO(1L, 1, LocalTime.of(8, 30), 95);
		List<ClassTimeDTO> classTimeDTOs = List.of(classTimeDTO);

		ClassTypeDTO classTypeDTO = new ClassTypeDTO(1L, "classTypeDTO");
		List<ClassTypeDTO> classTypeDTOs = List.of(classTypeDTO);

		BuildingDTO buildingDTO = new BuildingDTO(1L, "buildingDTOName", "buildingDTOAddress");
		ClassroomDTO classroomDTO = new ClassroomDTO(1L, "classroomDTOName", buildingDTO);
		List<ClassroomDTO> classroomDTOs = List.of(classroomDTO);

		// Create a list of ScheduledClassDTOs for testing
		ScheduledClassDTO scheduledClassDTO =
				ScheduledClassDTO.builder().id(scheduledClassId).courseDTO(courseDTO).teacherDTO(teacherDTO)
						.groupDTOS(groupDTOS).date(localDate).classTimeDTO(classTimeDTO).classTypeDTO(classTypeDTO)
						.classroomDTO(classroomDTO).build();

		when(scheduledClassService.findByIdAsDTO(scheduledClassId)).thenReturn(scheduledClassDTO);
		when(courseService.findAllAsDTO()).thenReturn(courseDTOS);
		when(teacherService.findAllAsDTO()).thenReturn(teacherDTOS);
		when(classroomService.findAllAsDTO()).thenReturn(classroomDTOs);
		when(classTimeService.findAllAsDTO()).thenReturn(classTimeDTOs);
		when(classTypeService.findAllAsDTO()).thenReturn(classTypeDTOs);
		when(groupService.findAllAsDTO()).thenReturn(groupDTOS);

		mockMvc.perform(MockMvcRequestBuilders.get("/classes/update/{id}", scheduledClassId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity", "courseDTOList", "teacherDTOList", "classroomDTOList",
						"classTimeDTOList", "classTypeDTOList", "groupDTOList"))
				.andExpect(view().name("classesUpdateForm")).andExpect(model().attribute("entity", scheduledClassDTO))
				.andExpect(model().attribute("courseDTOList", courseDTOS))
				.andExpect(model().attribute("teacherDTOList", teacherDTOS))
				.andExpect(model().attribute("classroomDTOList", classroomDTOs))
				.andExpect(model().attribute("classTimeDTOList", classTimeDTOs))
				.andExpect(model().attribute("classTypeDTOList", classTypeDTOs))
				.andExpect(model().attribute("groupDTOList", groupDTOS));

		verify(scheduledClassService, times(1)).findByIdAsDTO(scheduledClassId);
		verify(courseService, times(1)).findAllAsDTO();
		verify(teacherService, times(1)).findAllAsDTO();
		verify(classroomService, times(1)).findAllAsDTO();
		verify(classTimeService, times(1)).findAllAsDTO();
		verify(classTypeService, times(1)).findAllAsDTO();
		verify(groupService, times(1)).findAllAsDTO();
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSES"})
	public void update_whenScheduledClassServiceThrowValidationException_thenProcessForm() throws Exception {
		Long scheduledClassId = 1L;

		CourseDTO courseDTO = new CourseDTO(1L, "CourseDTOName");
		List<CourseDTO> courseDTOS = List.of(courseDTO);

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		TeacherDTO teacherDTO =
				new TeacherDTO(1L, "teacher@email.com", "teacherFirstName", "teacherLastName", roleDTO, true,
						courseDTOS);
		List<TeacherDTO> teacherDTOS = List.of(teacherDTO);

		DisciplineDTO disciplineDTO = new DisciplineDTO(1L, "disciplineDTOName");
		GroupDTO groupDTO = new GroupDTO(1L, "groupDTOName", disciplineDTO, courseDTOS);
		List<GroupDTO> groupDTOS = List.of(groupDTO);

		LocalDate localDate = LocalDate.of(2000, 1, 1);

		ClassTimeDTO classTimeDTO = new ClassTimeDTO(1L, 1, LocalTime.of(8, 30), 95);
		List<ClassTimeDTO> classTimeDTOs = List.of(classTimeDTO);

		ClassTypeDTO classTypeDTO = new ClassTypeDTO(1L, "classTypeDTO");
		List<ClassTypeDTO> classTypeDTOs = List.of(classTypeDTO);

		BuildingDTO buildingDTO = new BuildingDTO(1L, "buildingDTOName", "buildingDTOAddress");
		ClassroomDTO classroomDTO = new ClassroomDTO(1L, "classroomDTOName", buildingDTO);
		List<ClassroomDTO> classroomDTOs = List.of(classroomDTO);

		when(courseService.findByIdAsDTO(courseDTO.getId())).thenReturn(courseDTO);
		when(teacherService.findByIdAsDTO(teacherDTO.getId())).thenReturn(teacherDTO);
		when(groupService.findByIdAsDTO(groupDTO.getId())).thenReturn(groupDTO);
		when(classTimeService.findByIdAsDTO(classTimeDTO.getId())).thenReturn(classTimeDTO);
		when(classTypeService.findByIdAsDTO(classTypeDTO.getId())).thenReturn(classTypeDTO);
		when(classroomService.findByIdAsDTO(classroomDTO.getId())).thenReturn(classroomDTO);

		// Create a list of ScheduledClassDTOs for testing
		ScheduledClassDTO scheduledClassDTO =
				ScheduledClassDTO.builder().id(scheduledClassId).courseDTO(courseDTO).teacherDTO(teacherDTO)
						.groupDTOS(groupDTOS).date(localDate).classTimeDTO(classTimeDTO).classTypeDTO(classTypeDTO)
						.classroomDTO(classroomDTO).build();

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(scheduledClassService.save((ScheduledClassDTO) any())).thenThrow(validationException);
		when(scheduledClassService.findByIdAsDTO(scheduledClassId)).thenReturn(scheduledClassDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/update/{id}", scheduledClassId).with(csrf())
				.flashAttr("scheduledClassDTO", scheduledClassDTO)).andExpect(status().is3xxRedirection());

		verify(scheduledClassService, times(1)).save(scheduledClassDTO);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSES"})
	public void update_whenScheduledClassServiceThrowServiceException_thenProcessForm() throws Exception {
		Long scheduledClassId = 1L;

		CourseDTO courseDTO = new CourseDTO(1L, "CourseDTOName");
		List<CourseDTO> courseDTOS = List.of(courseDTO);

		RoleDTO roleDTO = new RoleDTO(1L, "roleDTOName");
		TeacherDTO teacherDTO =
				new TeacherDTO(1L, "teacher@email.com", "teacherFirstName", "teacherLastName", roleDTO, true,
						courseDTOS);
		List<TeacherDTO> teacherDTOS = List.of(teacherDTO);

		DisciplineDTO disciplineDTO = new DisciplineDTO(1L, "disciplineDTOName");
		GroupDTO groupDTO = new GroupDTO(1L, "groupDTOName", disciplineDTO, courseDTOS);
		List<GroupDTO> groupDTOS = List.of(groupDTO);

		LocalDate localDate = LocalDate.of(2000, 1, 1);

		ClassTimeDTO classTimeDTO = new ClassTimeDTO(1L, 1, LocalTime.of(8, 30), 95);
		List<ClassTimeDTO> classTimeDTOs = List.of(classTimeDTO);

		ClassTypeDTO classTypeDTO = new ClassTypeDTO(1L, "classTypeDTO");
		List<ClassTypeDTO> classTypeDTOs = List.of(classTypeDTO);

		BuildingDTO buildingDTO = new BuildingDTO(1L, "buildingDTOName", "buildingDTOAddress");
		ClassroomDTO classroomDTO = new ClassroomDTO(1L, "classroomDTOName", buildingDTO);
		List<ClassroomDTO> classroomDTOs = List.of(classroomDTO);

		when(courseService.findByIdAsDTO(courseDTO.getId())).thenReturn(courseDTO);
		when(teacherService.findByIdAsDTO(teacherDTO.getId())).thenReturn(teacherDTO);
		when(groupService.findByIdAsDTO(groupDTO.getId())).thenReturn(groupDTO);
		when(classTimeService.findByIdAsDTO(classTimeDTO.getId())).thenReturn(classTimeDTO);
		when(classTypeService.findByIdAsDTO(classTypeDTO.getId())).thenReturn(classTypeDTO);
		when(classroomService.findByIdAsDTO(classroomDTO.getId())).thenReturn(classroomDTO);

		// Create a list of ScheduledClassDTOs for testing
		ScheduledClassDTO scheduledClassDTO =
				ScheduledClassDTO.builder().id(scheduledClassId).courseDTO(courseDTO).teacherDTO(teacherDTO)
						.groupDTOS(groupDTOS).date(localDate).classTimeDTO(classTimeDTO).classTypeDTO(classTypeDTO)
						.classroomDTO(classroomDTO).build();

		ServiceException serviceException = new ServiceException("testException");

		when(scheduledClassService.save((ScheduledClassDTO) any())).thenThrow(serviceException);
		when(scheduledClassService.findByIdAsDTO(scheduledClassId)).thenReturn(scheduledClassDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/update/{id}", scheduledClassId).with(csrf())
				.flashAttr("scheduledClassDTO", scheduledClassDTO)).andExpect(status().is3xxRedirection());

		verify(scheduledClassService, times(1)).save(scheduledClassDTO);

	}
}

