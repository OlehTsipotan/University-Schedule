package com.university.schedule.repository;

import com.university.schedule.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduledClassRepository extends JpaRepository<ScheduledClass, Long> {

    ScheduledClass save(ScheduledClass ScheduledClass);

    Optional<ScheduledClass> findById(Long id);

    List<ScheduledClass> findByDateBetweenAndGroups(LocalDate startDate, LocalDate endDate, Group group);

    List<ScheduledClass> findByDateBetweenAndGroupsAndCourse(LocalDate startDate, LocalDate endDate, Group group, Course course);

    List<ScheduledClass> findByDateBetweenAndGroupsAndCourseAndClassType(LocalDate startDate, LocalDate endDate, Group group, Course course, ClassType classType);

    List<ScheduledClass> findByDateBetweenAndGroupsAndClassType(LocalDate startDate, LocalDate endDate, Group group, ClassType classType);

    List<ScheduledClass> findByDateBetweenAndTeacher(LocalDate startDate, LocalDate endDate, Teacher teacher);

    List<ScheduledClass> findByDateBetweenAndTeacherAndGroups(LocalDate startDate, LocalDate endDate, Teacher teacher, Group group);

    List<ScheduledClass> findByDateBetweenAndTeacherAndGroupsAndCourse(LocalDate startDate, LocalDate endDate, Teacher teacher, Group group, Course course);

    List<ScheduledClass> findByDateBetweenAndTeacherAndGroupsAndCourseAndClassType(LocalDate startDate, LocalDate endDate, Teacher teacher, Group group, Course course, ClassType classType);

    List<ScheduledClass> findByDateBetweenAndTeacherAndCourse(LocalDate startDate, LocalDate endDate, Teacher teacher, Course course);

    List<ScheduledClass> findByDateBetweenAndTeacherAndCourseAndClassType(LocalDate startDate, LocalDate endDate, Teacher teacher, Course course, ClassType classType);

    List<ScheduledClass> findAll();

    void deleteById(Long id);
}
