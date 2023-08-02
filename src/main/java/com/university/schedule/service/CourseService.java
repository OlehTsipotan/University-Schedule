package com.university.schedule.service;

import com.university.schedule.model.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    Long save(Course course);

    Course findById(Long id);

    Course findByName(String name);

    List<Course> findAll();

    void deleteById(Long id);

    List<Course> findByGroupsName(String groupName);
}
