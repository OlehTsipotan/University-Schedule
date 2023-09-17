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
public class RoleDTO {

	private Long id;

	@NonNull
	@NotBlank(message = "RoleDTO name must not be blank")
	@Size(max = 255)
	private String name;
}
