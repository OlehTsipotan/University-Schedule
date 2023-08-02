package com.university.schedule.repository;

import com.university.schedule.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Teacher save(Teacher teacher);

    List<Teacher> findAll();

    Optional<Teacher> findByEmailAndPassword(String email, String password);

    Optional<Teacher> findById(Long id);

    void deleteById(Long id);
}
