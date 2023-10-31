package com.university.schedule.visitor;

import com.university.schedule.model.Student;
import com.university.schedule.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultUserPageableStudentVisitor implements UserPageableStudentVisitor {

    private final StudentRepository studentRepository;

    @Override
    public List<Student> performActionForTeacher(Pageable pageable) {
        return studentRepository.findAll(pageable).toList();
    }

    @Override
    public List<Student> performActionForStudent(Student student, Pageable pageable) {
        return studentRepository.findByGroup(student.getGroup(), pageable).toList();
    }

    @Override
    public List<Student> performActionForUser(Pageable pageable) {
        return studentRepository.findAll(pageable).toList();
    }
}
