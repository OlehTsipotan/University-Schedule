package com.university.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class BuildingDTO {

	private Long id;

	@NonNull
	@NotBlank(message = "Building name must not be blank")
	@Size(max = 255)
	private String name;

	@NonNull
	@NotBlank(message = "Building address must not be blank")
	@Size(max = 255)
	private String address;

}
