package com.university.schedule.dto;

import com.university.schedule.model.Group;
import com.university.schedule.model.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StudentUpdateDTO extends UserUpdateDTO {

    private Group group;

    public StudentUpdateDTO(@NonNull Long id, @NonNull String email, @NonNull String firstName,
                            @NonNull String lastName, @NonNull Group group, @NonNull Role role,
                            @NonNull Boolean isEnable){
        super(id, email, firstName, lastName, role, isEnable);
        this.group = group;

    }
}
