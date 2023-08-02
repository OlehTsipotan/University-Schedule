package com.university.schedule.repository;

import com.university.schedule.model.ClassTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassTimeRepository extends JpaRepository<ClassTime, Long> {

    ClassTime save(ClassTime classTime);

    Optional<ClassTime> findById(Long id);

    Optional<ClassTime> findByOrderNumber(Integer order);

    List<ClassTime> findAll();

    void deleteById(Long id);
}
