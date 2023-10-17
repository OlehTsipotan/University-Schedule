package com.university.schedule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScheduledClassDTO {

	private Long id;

	@NotNull
	private CourseDTO courseDTO;

	@NotNull
	private TeacherDTO teacherDTO;

	@NotNull
	private ClassroomDTO classroomDTO;

	@NotNull
	private ClassTimeDTO classTimeDTO;

	@NotNull
	private LocalDate date;

	@NotNull
	private ClassTypeDTO classTypeDTO;

	@NotNull
	private List<GroupDTO> groupDTOS;

}

