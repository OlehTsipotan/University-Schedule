package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Course;
import com.university.schedule.model.Teacher;
import com.university.schedule.repository.TeacherRepository;
import com.university.schedule.utility.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DefaultTeacherService implements TeacherService {

    private final TeacherRepository teacherRepository;

    private final CourseService courseService;

    private final EntityValidator entityValidator;

    @Override
    public List<Teacher> findAll() {
        List<Teacher> teachers = execute(() -> teacherRepository.findAll());
        log.debug("Retrieved All {} Groups", teachers.size());
        return teachers;
    }

    @Override
    @Transactional
    public Long save(Teacher teacher) {
        entityValidator.validate(teacher);
        execute(() -> teacherRepository.save(teacher));
        log.info("saved {}", teacher);
        return teacher.getId();
    }

    @Override
    public Teacher findById(Long id) {
        Teacher teacher = execute(() -> teacherRepository.findById(id)).orElseThrow(
                () -> new ServiceException("Teacher not found"));
        log.debug("Retrieved {}", teacher);
        return teacher;
    }

    @Override
    public Teacher findByEmailAndPassword(String email, String password) {
        Teacher teacher = execute(() -> teacherRepository.findByEmailAndPassword(email, password)).orElseThrow(
                () -> new ServiceException("Teacher not found"));
        log.debug("Retrieved {}", teacher);
        return teacher;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        execute(() -> teacherRepository.deleteById(id));
        log.info("Deleted id = {}", id);
    }

    @Override
    @Transactional
    public boolean assignToCourse(Long teacherId, Long courseId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new ServiceException("There is no Teacher with id = " + teacherId));
        Course course = courseService.findById(courseId);
        boolean result = teacher.getCourses().add(course);

        save(teacher);
        log.info("{} assigned to {} - {}", teacher, course, result);
        return result;
    }

    @Override
    @Transactional
    public boolean removeFromCourse(Long teacherId, Long courseId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new ServiceException("There is no Teacher with id = " + teacherId));
        Course course = courseService.findById(courseId);
        boolean result = teacher.getCourses().remove(course);

        save(teacher);
        log.info("{} removed from {} - {}", teacher, course, result);
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
