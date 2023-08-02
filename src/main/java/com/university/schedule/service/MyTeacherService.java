package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Course;
import com.university.schedule.model.Teacher;
import com.university.schedule.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MyTeacherService implements TeacherService {

    private final TeacherRepository teacherRepository;

    private final CourseService courseService;

    private final Logger logger = LoggerFactory.getLogger(MyTeacherService.class);

    public MyTeacherService(TeacherRepository teacherRepository, CourseService courseService) {
        this.teacherRepository = teacherRepository;
        this.courseService = courseService;
    }

    @Override
    public List<Teacher> findAll() {
        List<Teacher> teachers = execute(() -> teacherRepository.findAll());
        logger.debug("Retrieved All {} Groups", teachers.size());
        return teachers;
    }

    @Override
    public Long save(Teacher teacher) {
        if (StringUtils.isEmpty(teacher.getEmail())) {
            throw new ServiceException("Email can`t be empty or null");
        }
        if (StringUtils.isEmpty(teacher.getPassword())) {
            throw new ServiceException("Password can`t be empty or null");
        }
        if (StringUtils.isEmpty(teacher.getFirstName())) {
            throw new ServiceException("FirstName can`t be empty or null");
        }
        if (StringUtils.isEmpty(teacher.getLastName())) {
            throw new ServiceException("LastName can`t be empty or null");
        }
        execute(() -> teacherRepository.save(teacher));
        logger.info("saved {}", teacher);
        return teacher.getId();
    }

    @Override
    public Teacher findById(Long id) {
        Teacher teacher = execute(() -> teacherRepository.findById(id)).orElseThrow(() -> new ServiceException("Teacher not found"));
        logger.debug("Retrieved {}", teacher);
        return teacher;
    }

    @Override
    public Teacher findByEmailAndPassword(String email, String password) {
        Teacher teacher = execute(() -> teacherRepository.findByEmailAndPassword(email, password)).orElseThrow(() -> new ServiceException("Teacher not found"));
        logger.debug("Retrieved {}", teacher);
        return teacher;
    }

    @Override
    public void deleteById(Long id) {
        execute(() -> teacherRepository.deleteById(id));
        logger.info("Deleted id = {}", id);
    }

    @Override
    public boolean assignToCourse(Long teacherId, Long courseId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new ServiceException("There is no Teacher with id = " + teacherId));
        Course course = courseService.findById(courseId);
        boolean result = teacher.getCourses().add(course);

        save(teacher);
        logger.info("{} assigned to {} - {}", teacher, course, result);
        return result;
    }

    @Override
    public boolean removeFromCourse(Long teacherId, Long courseId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new ServiceException("There is no Teacher with id = " + teacherId));
        Course course = courseService.findById(courseId);
        boolean result = teacher.getCourses().remove(course);

        save(teacher);
        logger.info("{} removed from {} - {}", teacher, course, result);
        return result;
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
