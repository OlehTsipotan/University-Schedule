package com.university.schedule.service;

import com.university.schedule.model.Course;
import com.university.schedule.model.Teacher;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface TeacherService {

    List<Teacher> findAll();

    List<Teacher> findAll(Sort sort);

    Long save(Teacher teacher);

    Teacher findById(Long id);

    Teacher findByEmailAndPassword(String email, String password);

    void deleteById(Long id);

    boolean assignToCourse(Long groupId, Long courseId);

    boolean removeFromCourse(Long groupId, Long courseId);

    List<Teacher> findByCourses(Course course);
}
