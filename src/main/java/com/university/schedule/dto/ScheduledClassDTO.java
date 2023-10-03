package com.university.schedule.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScheduledClassDTO {

	private Long id;

	private CourseDTO courseDTO;

	private TeacherDTO teacherDTO;

	private ClassroomDTO classroomDTO;

	private ClassTimeDTO classTimeDTO;

	private LocalDate date;

	private ClassTypeDTO classTypeDTO;

	private List<GroupDTO> groupDTOS;

}

