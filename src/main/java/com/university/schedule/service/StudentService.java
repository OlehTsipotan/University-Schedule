package com.university.schedule.service;

import com.university.schedule.model.Student;

import java.util.List;

public interface StudentService {

    List<Student> findAll();

    Long save(Student student);

    Student findById(Long id);

    Student findByEmailAndPassword(String email, String password);

    List<Student> findByGroupsName(String groupName);

    void deleteById(Long id);
}
