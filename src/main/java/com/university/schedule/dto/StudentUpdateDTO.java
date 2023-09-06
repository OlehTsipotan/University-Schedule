package com.university.schedule.dto;

import com.university.schedule.model.Group;
import lombok.*;

@Getter
@Setter
@ToString
public class StudentUpdateDTO extends UserUpdateDTO {

    private Group group;
}
