package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.*;
import com.university.schedule.repository.ScheduledClassRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Transactional
@Service
public class MyScheduledClassService implements ScheduledClassService {

    private final ScheduledClassRepository scheduledClassRepository;

    private final Logger logger = LoggerFactory.getLogger(MyScheduledClassService.class);

    public MyScheduledClassService(ScheduledClassRepository scheduledClassRepository) {
        this.scheduledClassRepository = scheduledClassRepository;
    }


    @Override
    public Long save(ScheduledClass ScheduledClass) {
        execute(() -> scheduledClassRepository.save(ScheduledClass));
        logger.info("saved {}", ScheduledClass);
        return ScheduledClass.getId();
    }

    @Override
    public ScheduledClass findById(Long id) {
        ScheduledClass scheduledClass = execute(() -> scheduledClassRepository.findById(id)).orElseThrow(() -> new ServiceException("ScheduledClass not found"));
        logger.debug("Retrieved {}", scheduledClass);
        return scheduledClass;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndGroup(LocalDate startDate, LocalDate endDate, Group group) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findByDateBetweenAndGroups(startDate, endDate, group));
        logger.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndGroupAndCourse(LocalDate startDate, LocalDate endDate, Group group, Course course) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findByDateBetweenAndGroupsAndCourse(startDate, endDate, group, course));
        logger.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndGroupAndCourseAndClassType(LocalDate startDate, LocalDate endDate, Group group, Course course, ClassType classType) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findByDateBetweenAndGroupsAndCourseAndClassType(startDate, endDate, group, course, classType));
        logger.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndGroupAndClassType(LocalDate startDate, LocalDate endDate, Group group, ClassType classType) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findByDateBetweenAndGroupsAndClassType(startDate, endDate, group, classType));
        logger.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacher(LocalDate startDate, LocalDate endDate, Teacher teacher) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findByDateBetweenAndTeacher(startDate, endDate, teacher));
        logger.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacherAndGroup(LocalDate startDate, LocalDate endDate, Teacher teacher, Group group) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findByDateBetweenAndTeacherAndGroups(startDate, endDate, teacher, group));
        logger.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacherAndGroupAndCourse(LocalDate startDate, LocalDate endDate, Teacher teacher, Group group, Course course) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findByDateBetweenAndTeacherAndGroupsAndCourse(startDate, endDate, teacher, group, course));
        logger.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacherAndGroupAndCourseAndClassType(LocalDate startDate, LocalDate endDate, Teacher teacher, Group group, Course course, ClassType classType) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findByDateBetweenAndTeacherAndGroupsAndCourseAndClassType(startDate, endDate, teacher, group, course, classType));
        logger.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacherAndCourse(LocalDate startDate, LocalDate endDate, Teacher teacher, Course course) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findByDateBetweenAndTeacherAndCourse(startDate, endDate, teacher, course));
        logger.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findByDateBetweenAndTeacherAndCourseAndClassType(LocalDate startDate, LocalDate endDate, Teacher teacher, Course course, ClassType classType) {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findByDateBetweenAndTeacherAndCourseAndClassType(startDate, endDate, teacher, course, classType));
        logger.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public List<ScheduledClass> findAll() {
        List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findAll());
        logger.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
        return scheduledClasses;
    }

    @Override
    public void deleteById(Long id) {
        execute(() -> scheduledClassRepository.deleteById(id));
        logger.info("Deleted id = {}", id);
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
