package com.university.schedule.repository;

import com.university.schedule.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmailAndPassword(String email, String password);
}
