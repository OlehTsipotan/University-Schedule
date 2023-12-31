package com.university.schedule.dto;

import com.university.schedule.validation.Password;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
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

    @Password(message = "Minimum 4 characters, at least 1 letter and 1 number")
    @NotBlank(message = "User password must not be blank")
    @Size(min = 4, max = 25)
    private String password;

    @NotBlank(message = "User password must not be blank")
    @Size(min = 4, max = 25)
    private String confirmationPassword;

    private RoleDTO roleDTO;

}
