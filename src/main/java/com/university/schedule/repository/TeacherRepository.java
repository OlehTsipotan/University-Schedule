package com.university.schedule.repository;

import com.university.schedule.model.Course;
import com.university.schedule.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmail(String email);
    List<Teacher> findByCourses(Course course);
}
