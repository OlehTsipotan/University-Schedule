package com.university.schedule.dto;

import com.university.schedule.model.Course;
import com.university.schedule.model.Group;
import com.university.schedule.model.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TeacherUpdateDTO extends UserUpdateDTO {

    private List<Course> courses;

    public TeacherUpdateDTO(@NonNull Long id, @NonNull String email, @NonNull String firstName,
                            @NonNull String lastName, @NonNull List<Course> courses, @NonNull Role role,
                            @NonNull Boolean isEnable){
        super(id, email, firstName, lastName, role, isEnable);
        this.courses = courses;
    }
}


