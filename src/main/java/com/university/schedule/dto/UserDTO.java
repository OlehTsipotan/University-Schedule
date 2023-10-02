package com.university.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	private Long id;

	@NonNull
	@NotBlank(message = "User email must not be blank")
	@Size(max = 255)
	private String email;

	@NonNull
	@NotBlank(message = "User firstName must not be blank")
	@Size(max = 255)
	private String firstName;

	@NonNull
	@NotBlank(message = "User lastName must not be blank")
	@Size(max = 255)
	private String lastName;

	private RoleDTO roleDTO;
	@Getter(AccessLevel.NONE)
	private Boolean isEnable;

	public Boolean isEnable() {
		return this.isEnable;
	}

	public String getFullName() {
		return firstName + ' ' + lastName;
	}
}
