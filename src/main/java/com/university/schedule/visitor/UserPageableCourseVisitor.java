package com.university.schedule.visitor;

import com.university.schedule.model.Course;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserPageableCourseVisitor {

    List<Course> performActionForTeacher(Teacher teacher, Pageable pageable);

    List<Course> performActionForStudent(Student student, Pageable pageable);

    List<Course> performActionForUser(Pageable pageable);
}
