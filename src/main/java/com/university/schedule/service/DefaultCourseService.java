package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Course;
import com.university.schedule.repository.CourseRepository;
import com.university.schedule.utility.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefaultCourseService implements CourseService{

    private final CourseRepository courseRepository;

    private final EntityValidator entityValidator;

    @Override
    @Transactional
    public Long save(Course course) {
        entityValidator.validate(course);
        execute(() -> courseRepository.save(course));
        log.info("saved {}", course);
        return course.getId();
    }

    @Override
    public Course findById(Long id) {
        Course course = execute(() -> courseRepository.findById(id)).orElseThrow(
                () -> new ServiceException("Course not found"));
        log.debug("Retrieved {}", course);
        return course;
    }

    @Override
    public Course findByName(String name) {
        Course course = execute(() -> courseRepository.findByName(name)).orElseThrow(
                () -> new ServiceException("Course not found"));
        log.debug("Retrieved {}", course);
        return course;
    }

    @Override
    public List<Course> findAll() {
        List<Course> courses = execute(() -> courseRepository.findAll());
        log.debug("Retrieved All {} Groups", courses.size());
        return courses;
    }

    @Override
    public List<Course> findAll(Sort sort) {
        List<Course> courses = execute(() -> courseRepository.findAll(sort));
        log.debug("Retrieved All {} Groups", courses.size());
        return courses;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try{
            findById(id);
        } catch (ServiceException e){
            throw new ServiceException("There is no Course to delete with id = "+ id);
        }
        execute(() -> courseRepository.deleteById(id));
        log.info("Deleted id = {}", id);
    }

    @Override
    public List<Course> findByGroupsName(String groupName) {
        List<Course> courses = execute(() -> courseRepository.findByGroupsName(groupName));
        log.debug("Retrieved All {} Groups", courses.size());
        return courses;
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
