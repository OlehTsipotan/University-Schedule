package com.university.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {

	private Long id;

	@NotBlank(message = "Group name must not be blank")
	@Size(max = 255)
	private String name;

	@EqualsAndHashCode.Exclude
	private DisciplineDTO disciplineDTO;

	@EqualsAndHashCode.Exclude
	private List<CourseDTO> courseDTOS;
}
