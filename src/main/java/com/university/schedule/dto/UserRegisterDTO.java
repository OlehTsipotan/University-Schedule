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
public class UserRegisterDTO {

	@NotBlank(message = "User email must not be blank")
	@Size(max = 255)
	private String email;

	@NotBlank(message = "User firstName must not be blank")
	@Size(max = 255)
	private String firstName;

	@NotBlank(message = "User lastName must not be blank")
	@Size(max = 255)
	private String lastName;

	@NotBlank(message = "User password must not be blank")
	@Size(min = 4, max = 25)
	private String password;

	@NotBlank(message = "User password must not be blank")
	@Size(min = 4, max = 25)
	private String confirmationPassword;

	private RoleDTO roleDTO;

}
