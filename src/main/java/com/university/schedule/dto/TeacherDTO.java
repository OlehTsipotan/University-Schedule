package com.university.schedule.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDTO extends UserDTO {

    private List<CourseDTO> courseDTOS;

    public TeacherDTO(Long id, String email, String firstName, String lastName, RoleDTO roleDTO, Boolean isEnable,
                      List<CourseDTO> courseDTOS) {
        super(id, email, firstName, lastName, roleDTO, isEnable);
        this.courseDTOS = courseDTOS;
    }

}
