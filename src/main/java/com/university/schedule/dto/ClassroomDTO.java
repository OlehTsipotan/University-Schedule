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
public class ClassroomDTO {

	private Long id;

	@NonNull
	@NotBlank(message = "Classroom name must not be blank")
	@Size(max = 255)
	private String name;

	private BuildingDTO buildingDTO;

}
