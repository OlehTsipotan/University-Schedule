package com.university.schedule.service;

import com.university.schedule.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduledClassService {

    Long save(ScheduledClass ScheduledClass);

    ScheduledClass findById(Long id);

    ScheduledClass findByDateAndClassTimeAndTeacher(LocalDate date, ClassTime classTime, Teacher teacher);

    List<ScheduledClass> findByDateBetweenAndGroup(LocalDate startDate, LocalDate endDate, Group group);

    List<ScheduledClass> findByDateBetweenAndGroupAndCourse(LocalDate startDate, LocalDate endDate, Group group, Course course);

    List<ScheduledClass> findByDateBetweenAndGroupAndCourseAndClassType(LocalDate startDate, LocalDate endDate, Group group, Course course, ClassType classType);

    List<ScheduledClass> findByDateBetweenAndGroupAndClassType(LocalDate startDate, LocalDate endDate, Group group, ClassType classType);


    List<ScheduledClass> findByDateBetweenAndTeacher(LocalDate startDate, LocalDate endDate, Teacher teacher);

    List<ScheduledClass> findByDateBetweenAndTeacherAndGroup(LocalDate startDate, LocalDate endDate, Teacher teacher, Group group);

    List<ScheduledClass> findByDateBetweenAndTeacherAndGroupAndCourse(LocalDate startDate, LocalDate endDate, Teacher teacher, Group group, Course course);

    List<ScheduledClass> findByDateBetweenAndTeacherAndGroupAndCourseAndClassType(LocalDate startDate, LocalDate endDate, Teacher teacher, Group group, Course course, ClassType classType);

    List<ScheduledClass> findByDateBetweenAndTeacherAndCourse(LocalDate startDate, LocalDate endDate, Teacher teacher, Course course);

    List<ScheduledClass> findByDateBetweenAndTeacherAndCourseAndClassType(LocalDate startDate, LocalDate endDate, Teacher teacher, Course course, ClassType classType);

    List<ScheduledClass> findAll();

    Page<ScheduledClass> findAll(Pageable pageable);

    List<ScheduledClass> findAll(Sort sort);

    void deleteById(Long id);
}
