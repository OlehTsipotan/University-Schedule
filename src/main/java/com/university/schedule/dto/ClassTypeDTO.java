package com.university.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClassTypeDTO {

	private Long id;

	@NonNull
	@NotBlank(message = "ClassType name must not be blank")
	@Size(max = 255)
	private String name;
}
