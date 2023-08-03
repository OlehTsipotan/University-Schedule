package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Student;
import com.university.schedule.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefaultStudentService implements StudentService {


    private final StudentRepository studentRepository;

    @Override
    public List<Student> findAll() {
        List<Student> students = execute(() -> studentRepository.findAll());
        log.debug("Retrieved All {} Groups", students.size());
        return students;
    }

    @Override
    @Transactional
    public Long save(Student student) {
        if (StringUtils.isEmpty(student.getEmail())) {
            throw new ServiceException("Email can`t be empty or null");
        }
        if (StringUtils.isEmpty(student.getPassword())) {
            throw new ServiceException("Password can`t be empty or null");
        }
        if (StringUtils.isEmpty(student.getFirstName())) {
            throw new ServiceException("FirstName can`t be empty or null");
        }
        if (StringUtils.isEmpty(student.getLastName())) {
            throw new ServiceException("LastName can`t be empty or null");
        }
        execute(() -> studentRepository.save(student));
        log.info("saved {}", student);
        return student.getId();
    }

    @Override
    public Student findById(Long id) {
        Student student = execute(() -> studentRepository.findById(id)).orElseThrow(
                () -> new ServiceException("Student not found"));
        log.debug("Retrieved {}", student);
        return student;
    }

    @Override
    public Student findByEmailAndPassword(String email, String password) {
        Student student = execute(() -> studentRepository.findByEmailAndPassword(email, password)).orElseThrow(
                () -> new ServiceException("Student not found"));
        log.debug("Retrieved {}", student);
        return student;
    }

    @Override
    public List<Student> findByGroupsName(String groupName) {
        List<Student> students = execute(() -> studentRepository.findByGroupsName(groupName));
        log.debug("Retrieved {}", students);
        return students;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        execute(() -> studentRepository.deleteById(id));
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