package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Course;
import com.university.schedule.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MyCourseService implements CourseService{

    private final CourseRepository courseRepository;

    private final Logger logger = LoggerFactory.getLogger(MyCourseService.class);

    public MyCourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }


    @Override
    public Long save(Course course) {
        if (StringUtils.isEmpty(course.getName())) {
            throw new ServiceException("courseName can`t be empty or null");
        }
        execute(() -> courseRepository.save(course));
        logger.info("saved {}", course);
        return course.getId();
    }

    @Override
    public Course findById(Long id) {
        Course course = execute(() -> courseRepository.findById(id)).orElseThrow(() -> new ServiceException("Course not found"));
        logger.debug("Retrieved {}", course);
        return course;
    }

    @Override
    public Course findByName(String name) {
        Course course = execute(() -> courseRepository.findByName(name)).orElseThrow(() -> new ServiceException("Course not found"));
        logger.debug("Retrieved {}", course);
        return course;
    }

    @Override
    public List<Course> findAll() {
        List<Course> courses = execute(() -> courseRepository.findAll());
        logger.debug("Retrieved All {} Groups", courses.size());
        return courses;
    }

    @Override
    public void deleteById(Long id) {
        execute(() -> courseRepository.deleteById(id));
        logger.info("Deleted id = {}", id);
    }

    @Override
    public List<Course> findByGroupsName(String groupName) {
        List<Course> courses = execute(() -> courseRepository.findByGroupsName(groupName));
        logger.debug("Retrieved All {} Groups", courses.size());
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
