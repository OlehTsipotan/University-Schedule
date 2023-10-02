package com.university.schedule.repository;

import com.university.schedule.model.ClassTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassTimeRepository extends JpaRepository<ClassTime, Long> {

	Optional<ClassTime> findByOrderNumber(Integer order);

}
