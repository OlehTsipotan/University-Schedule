package com.university.schedule.repository;

import com.university.schedule.model.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {
	Optional<ClassType> findByName(String name);
}
