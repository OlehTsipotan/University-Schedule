package com.university.schedule.repository;

import com.university.schedule.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Course save(Course course);

    Optional<Course> findById(Long id);

    Optional<Course> findByName(String name);

    List<Course> findAll();

    void deleteById(Long id);

    List<Course> findByGroupsName(String groupName);
}
