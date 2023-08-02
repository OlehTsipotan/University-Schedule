package com.university.schedule.repository;

import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    Classroom save(Classroom classroom);

    Optional<Classroom> findById(Long id);

    List<Classroom> findByBuilding(Building building);

    List<Classroom> findAll();

    void deleteById(Long id);
}
