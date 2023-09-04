package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.*;
import com.university.schedule.repository.ScheduledClassRepository;
import com.university.schedule.validation.EntityValidator;
import com.university.schedule.validation.ScheduledClassEntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DefaultScheduledClassService implements ScheduledClassService {

    private final ScheduledClassRepository scheduledClassRepository;

    private final ScheduledClassEntityValidator scheduledClassEntityValidator;

    @Override
    @Transactional
    public Long save(ScheduledClass scheduledClass) {
        execute(() -> {
            scheduledClassEntityValidator.validate(scheduledClass);
            scheduledClassRepository.save(scheduledClass);
        });
        log.info("saved {}", scheduledClass);
        return scheduledClass.getId();
    }

    @Override
    public ScheduledClass findById(Long id) {
        ScheduledClass scheduledClass = execute(() -> scheduledClassRepository.findById(id)).orElseThrow(
                () -> new ServiceException("ScheduledClass not found"));
        log.debug("Retrieved {}", scheduledClass);
        return scheduledClass;
    }

    @Override
    public ScheduledClass findByDateAndClassTimeAndTeacher(LocalDate date, ClassTime classTime, Teacher teacher) {
        ScheduledClass scheduledClass = execute(() -> scheduledClassRepository.findByDateAndClassTimeAndTeacher(date, classTime, teacher)).orElseThrow(
                () -> new ServiceException("ScheduledClass not found"));
        log.debug("Retrieved {}", scheduledClass);
        return scheduledClass;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndGroup(LocalDate startDate, LocalDate endDate, Group group) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository
                .findByDateBetweenAndGroups(startDate, endDate, group));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndGroupAndCourse
            (LocalDate startDate, LocalDate endDate, Group group, Course course) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository
                .findByDateBetweenAndGroupsAndCourse(startDate, endDate, group, course));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndGroupAndCourseAndClassType
            (LocalDate startDate, LocalDate endDate, Group group, Course course, ClassType classType) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository
                .findByDateBetweenAndGroupsAndCourseAndClassType(startDate, endDate, group, course, classType));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndGroupAndClassType
            (LocalDate startDate, LocalDate endDate, Group group, ClassType classType) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository
                .findByDateBetweenAndGroupsAndClassType(startDate, endDate, group, classType));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacher(LocalDate startDate, LocalDate endDate, Teacher teacher) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository
                .findByDateBetweenAndTeacher(startDate, endDate, teacher));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacherAndGroup
            (LocalDate startDate, LocalDate endDate, Teacher teacher, Group group) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository
                .findByDateBetweenAndTeacherAndGroups(startDate, endDate, teacher, group));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacherAndGroupAndCourse
            (LocalDate startDate, LocalDate endDate, Teacher teacher, Group group, Course course) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository
                .findByDateBetweenAndTeacherAndGroupsAndCourse(startDate, endDate, teacher, group, course));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacherAndGroupAndCourseAndClassType
            (LocalDate startDate, LocalDate endDate, Teacher teacher, Group group, Course course, ClassType classType) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository
                .findByDateBetweenAndTeacherAndGroupsAndCourseAndClassType
                        (startDate, endDate, teacher, group, course, classType));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacherAndCourse
            (LocalDate startDate, LocalDate endDate, Teacher teacher, Course course) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository
                .findByDateBetweenAndTeacherAndCourse(startDate, endDate, teacher, course));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacherAndCourseAndClassType(
            LocalDate startDate, LocalDate endDate, Teacher teacher, Course course, ClassType classType) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository
                .findByDateBetweenAndTeacherAndCourseAndClassType(startDate, endDate, teacher, course, classType));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findAll() {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findAll());
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public Page<ScheduledClass> findAll(Pageable pageable) {
        Page<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findAll(pageable));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.getTotalElements());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findAll(Sort sort) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findAll(sort));
        log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try{
            findById(id);
        } catch (ServiceException e){
            throw new ServiceException("There is no ScheduledClass to delete with id = "+ id);
        }
        execute(() -> scheduledClassRepository.deleteById(id));
        log.info("Deleted id = {}", id);
    }

    private <T> T execute(DaoSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (DataAccessException e) {
            throw new ServiceException("DAO operation failed", e);
        }
    }

    private void execute(DaoProcessor processor) {
        try {
            processor.process();
        } catch (DataAccessException e) {
            throw new ServiceException("DAO operation failed", e);
        }
    }

    @FunctionalInterface
    public interface DaoSupplier<T> {
        T get();
    }

    @FunctionalInterface
    public interface DaoProcessor {
        void process();
    }
}
