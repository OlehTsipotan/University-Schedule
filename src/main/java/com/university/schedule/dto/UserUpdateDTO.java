package com.university.schedule.dto;

import com.university.schedule.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    private Long id;

    @NotBlank(message = "User email must not be blank")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "User firstName must not be blank")
    @Size(max = 255)
    private String firstName;

    @NotBlank(message = "User lastName must not be blank")
    @Size(max = 255)
    private String lastName;

    private Role role;
    @Getter(AccessLevel.NONE)
    private Boolean isEnable;

    public Boolean isEnable(){
        return this.isEnable;
    }

}
