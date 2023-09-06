package com.university.schedule.dto;

import com.university.schedule.model.Course;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class TeacherUpdateDTO extends UserUpdateDTO {

    private List<Course> courses;
}
