package com.university.schedule.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {

    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String groupName;

    @Getter(AccessLevel.NONE)
    private Boolean isEnable;

    public Boolean isEnable(){
        return this.isEnable;
    }
}
