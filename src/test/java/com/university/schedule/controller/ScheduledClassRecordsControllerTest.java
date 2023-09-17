package com.university.schedule.controller;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.*;
import com.university.schedule.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
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
	private ScheduledClassDTOService scheduledClassDTOService;

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
		// Create a list of ScheduledClassDTOs for testing
		List<ScheduledClassDTO> scheduledClassDTOs = new ArrayList<>();
		scheduledClassDTOs.add(ScheduledClassDTO.builder().id(1L).courseName("Course 1").teacherFullName("Teacher 1")
				.groupNames(List.of("Group 1", "Group 2")).date("Date 1").classTimeOrderNumber(1)
				.classTypeName("ClassType 1").classroomName("Classroom 1").classroomBuildingName("Building 1").build());
		scheduledClassDTOs.add(ScheduledClassDTO.builder().id(2L).courseName("Course 2").teacherFullName("Teacher 2")
				.groupNames(List.of("Group 3", "Group 4")).date("Date 2").classTimeOrderNumber(2)
				.classTypeName("ClassType 2").classroomName("Classroom 2").classroomBuildingName("Building 2").build());

		// Mock the behavior of scheduledClassDTOService
		when(scheduledClassDTOService.findAll(any(Pageable.class))).thenReturn(scheduledClassDTOs);

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

		List<Course> courses = new ArrayList<>();
		courses.add(new Course(1L, "Course 1"));

		List<Teacher> teachers = new ArrayList<>();
		teachers.add(new Teacher(1L, "email1", "password1", "teacherFirstName", "teacherLastName"));

		List<ClassroomDTO> classroomDTOs = new ArrayList<>();
		classroomDTOs.add(ClassroomDTO.builder().id(1L).name("Classroom A").buildingName("Builindg A").build());

		List<ClassTimeDTO> classTimeDTOs = new ArrayList<>();
		classTimeDTOs.add(new ClassTimeDTO(1L, 1, LocalTime.of(8, 30), 95));

		List<ClassTypeDTO> classTypeDTOs = new ArrayList<>();
		classTypeDTOs.add(new ClassTypeDTO(1L, "classType 1"));

		Discipline discipline = new Discipline(1L, "Discipline 1");

		List<Group> groups = new ArrayList<>();
		groups.add(new Group(1L, "Group 1", discipline));

		ScheduledClass scheduledClass =
				ScheduledClass.builder().id(scheduledClassId).course(courses.get(0)).teacher(teachers.get(0))
						.classroom(new Classroom(1L, "Classroom 1", new Building(1L, "Building 1", "Address 1")))
						.classTime(new ClassTime(1L, 1, LocalTime.of(8, 0), Duration.ofMinutes(95)))
						.date(LocalDate.of(2023, 1, 1)).classType(classTypeDTOs.get(0)).groups(new HashSet<>(groups))
						.build();

		when(scheduledClassService.findById(scheduledClassId)).thenReturn(scheduledClass);
		when(courseService.findAll()).thenReturn(courses);
		when(teacherService.findAll()).thenReturn(teachers);
		when(classroomService.findAllAsDTO()).thenReturn(classroomDTOs);
		when(classTimeService.findAllAsDTO()).thenReturn(classTimeDTOs);
		when(classTypeService.findAllAsDTO()).thenReturn(classTypeDTOs);
		when(groupService.findAll()).thenReturn(groups);

		mockMvc.perform(MockMvcRequestBuilders.get("/classes/update/{id}", scheduledClassId)).andExpect(status().isOk())
				.andExpect(model().attributeExists("entity", "courses", "teachers", "classrooms", "classtimes",
						"classtypes", "groups")).andExpect(view().name("classesUpdateForm"))
				.andExpect(model().attribute("entity", scheduledClass)).andExpect(model().attribute("courses", courses))
				.andExpect(model().attribute("teachers", teachers))
				.andExpect(model().attribute("classrooms", classroomDTOs))
				.andExpect(model().attribute("classtimes", classTimeDTOs))
				.andExpect(model().attribute("classtypes", classTypeDTOs)).andExpect(model().attribute("groups", groups));

		verify(scheduledClassService, times(1)).findById(scheduledClassId);
		verify(courseService, times(1)).findAll();
		verify(teacherService, times(1)).findAll();
		verify(classroomService, times(1)).findAllAsDTO();
		verify(classTimeService, times(1)).findAllAsDTO();
		verify(classTypeService, times(1)).findAllAsDTO();
		verify(groupService, times(1)).findAll();
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSES"})
	public void update_whenScheduledClassServiceThrowValidationException_thenProcessForm() throws Exception {
		Long scheduledClassId = 1L;

		List<Course> courses = new ArrayList<>();
		courses.add(new Course(1L, "Course 1"));

		List<Teacher> teachers = new ArrayList<>();
		teachers.add(new Teacher(1L, "email1", "password1", "teacherFirstName", "teacherLastName"));

		List<ClassroomDTO> classroomDTOs = new ArrayList<>();
		classroomDTOs.add(ClassroomDTO.builder().id(1L).name("Classroom A").buildingName("Builindg A").build());

		List<ClassTimeDTO> classTimeDTOs = new ArrayList<>();
		classTimeDTOs.add(new ClassTimeDTO(1L, 1, LocalTime.of(8, 30), 95));

		List<ClassTypeDTO> classTypeDTOs = new ArrayList<>();
		classTypeDTOs.add(new ClassTypeDTO(1L, "classType 1"));

		Discipline discipline = new Discipline(1L, "Discipline 1");

		List<Group> groups = new ArrayList<>();
		groups.add(new Group(1L, "Group 1", discipline));

		ScheduledClass scheduledClass =
				ScheduledClass.builder().id(scheduledClassId).course(courses.get(0)).teacher(teachers.get(0))
						.classroom(new Classroom(1L, "Classroom 1", new Building(1L, "Building 1", "Address 1")))
						.classTime(new ClassTime(1L, 1, LocalTime.of(8, 0), Duration.ofMinutes(95)))
						.date(LocalDate.of(2023, 1, 1)).classType(classTypeDTOs.get(0)).groups(new HashSet<>(groups))
						.build();

		ValidationException validationException = new ValidationException("testException", List.of("myError"));

		when(scheduledClassService.save(any())).thenThrow(validationException);
		when(scheduledClassService.findById(scheduledClassId)).thenReturn(scheduledClass);

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/update/{id}", scheduledClassId).with(csrf())
						.flashAttr("scheduledClass", scheduledClass)).andExpect(status().isOk())
				.andExpect(model().attributeExists("validationServiceErrors"))
				.andExpect(model().attribute("validationServiceErrors", validationException.getViolations()))
				.andExpect(model().attributeExists("entity")).andExpect(model().attribute("entity", scheduledClass))
				.andExpect(view().name("classesUpdateForm"));

		verify(scheduledClassService, times(1)).save(scheduledClass);
	}

	@Test
	@WithMockUser(username = "username", authorities = {"EDIT_CLASSES"})
	public void update_whenScheduledClassServiceThrowServiceException_thenProcessForm() throws Exception {

		Long scheduledClassId = 1L;

		List<Course> courses = new ArrayList<>();
		courses.add(new Course(1L, "Course 1"));

		List<Teacher> teachers = new ArrayList<>();
		teachers.add(new Teacher(1L, "email1", "password1", "teacherFirstName", "teacherLastName"));

		List<ClassroomDTO> classroomDTOs = new ArrayList<>();
		classroomDTOs.add(new ClassroomDTO(1L, "Classroom 1", "Building 1"));

		List<ClassTimeDTO> classTimeDTOs = new ArrayList<>();
		classTimeDTOs.add(new ClassTimeDTO(1L, 1, "startTime", 95));

		List<ClassType> classTypes = new ArrayList<>();
		classTypes.add(new ClassType(1L, "classType 1"));

		Discipline discipline = new Discipline(1L, "Discipline 1");

		List<Group> groups = new ArrayList<>();
		groups.add(new Group(1L, "Group 1", discipline));

		ScheduledClass scheduledClass =
				ScheduledClass.builder().id(scheduledClassId).course(courses.get(0)).teacher(teachers.get(0))
						.classroom(new Classroom(1L, "Classroom 1", new Building(1L, "Building 1", "Address 1")))
						.classTime(new ClassTime(1L, 1, LocalTime.of(8, 0), Duration.ofMinutes(95)))
						.date(LocalDate.of(2023, 1, 1)).classType(classTypes.get(0)).groups(new HashSet<>(groups))
						.build();

		ServiceException serviceException = new ServiceException("testException");

		when(scheduledClassService.save(any())).thenThrow(serviceException);
		when(scheduledClassService.findById(scheduledClassId)).thenReturn(scheduledClass);

		mockMvc.perform(MockMvcRequestBuilders.post("/classes/update/{id}", scheduledClassId).with(csrf())
						.flashAttr("scheduledClass", scheduledClass)).andExpect(status().isOk())
				.andExpect(model().attributeExists("serviceError"))
				.andExpect(model().attribute("serviceError", serviceException.getMessage()))
				.andExpect(model().attribute("entity", scheduledClass)).andExpect(view().name("classesUpdateForm"));
	}
}

