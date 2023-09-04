package com.university.schedule.service;

import com.university.schedule.model.Course;
import com.university.schedule.model.Group;
import com.university.schedule.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    Long save(Course course);

    Course findById(Long id);

    Course findByName(String name);

    List<Course> findAll();

    List<Course> findByTeacher(Teacher teacher);

    List<Course> findByGroup(Group group);

    List<Course> findAll(Sort sort);

    Page<Course> findAll(Pageable pageable);

    void deleteById(Long id);

    List<Course> findByGroupsName(String groupName);
}
