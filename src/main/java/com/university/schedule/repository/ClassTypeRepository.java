package com.university.schedule.repository;

import com.university.schedule.model.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {


    ClassType save(ClassType classType);

    Optional<ClassType> findById(Long id);

    Optional<ClassType> findByName(String name);

    List<ClassType> findAll();

    void deleteById(Long id);
}
