package com.university.schedule.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDTO {

    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private List<String> courseNames;

    @Getter(AccessLevel.NONE)
    private Boolean isEnable;

    public Boolean isEnable(){
        return this.isEnable;
    }
}
