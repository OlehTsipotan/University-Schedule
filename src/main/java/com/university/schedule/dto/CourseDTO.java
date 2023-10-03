package com.university.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseDTO {

	private Long id;

	@NotBlank(message = "Course name must not be blank")
	@Size(max = 255)
	private String name;
}
