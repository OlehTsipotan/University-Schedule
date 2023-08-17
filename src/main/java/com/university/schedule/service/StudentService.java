package com.university.schedule.service;

import com.university.schedule.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface StudentService {

    List<Student> findAll();

    List<Student> findAll(Sort sort);

    Page<Student> findAll(Pageable pageable);

    Long save(Student student);

    Student findById(Long id);

    Student findByEmailAndPassword(String email, String password);

    List<Student> findByGroupsName(String groupName);

    void deleteById(Long id);
}
