package com.university.schedule.repository;

import com.university.schedule.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisciplineRepository extends JpaRepository<Discipline, Long> {

    Optional<Discipline> findByName(String name);
}
