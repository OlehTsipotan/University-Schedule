package com.university.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {

	private Long id;

	@NotBlank(message = "Course name must not be blank")
	@Size(max = 255)
	private String name;
}
