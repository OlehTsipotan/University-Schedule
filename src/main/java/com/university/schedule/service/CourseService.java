package com.university.schedule.service;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.model.Course;
import com.university.schedule.model.Group;
import com.university.schedule.model.Teacher;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    Long save(Course course);

    Long save(CourseDTO courseDTO);

    CourseDTO findByIdAsDTO(Long id);

    List<Course> findByGroupsName(String groupName);

    List<Course> findByGroup(Group group);

    List<Course> findAll();

    List<CourseDTO> findAllAsDTO();

    List<Course> findByTeacher(Teacher teacher);

    List<CourseDTO> findAllAsDTO(String email, Pageable pageable);

    void deleteById(Long id);


}
