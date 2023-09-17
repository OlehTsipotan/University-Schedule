package com.university.schedule.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDTO extends UserDTO {

	private List<CourseDTO> courseDTOS;
}
