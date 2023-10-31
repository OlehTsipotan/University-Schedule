package com.university.schedule.visitor;

import com.university.schedule.model.Student;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserPageableStudentVisitor {

    List<Student> performActionForTeacher(Pageable pageable);

    List<Student> performActionForStudent(Student student, Pageable pageable);

    List<Student> performActionForUser(Pageable pageable);
}
