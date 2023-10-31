package com.university.schedule.visitor;

import com.university.schedule.model.Course;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultUserPageableCourseVisitor implements UserPageableCourseVisitor {

    private final CourseRepository courseRepository;

    @Override
    public List<Course> performActionForTeacher(Teacher teacher, Pageable pageable) {
        return courseRepository.findByTeachers(teacher, pageable).toList();
    }

    @Override
    public List<Course> performActionForStudent(Student student, Pageable pageable) {
        return courseRepository.findByGroups(student.getGroup(), pageable).toList();
    }

    @Override
    public List<Course> performActionForUser(Pageable pageable) {
        return courseRepository.findAll(pageable).toList();
    }
}
