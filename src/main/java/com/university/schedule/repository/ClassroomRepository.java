package com.university.schedule.repository;

import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

	Optional<Classroom> findByNameAndBuilding(String name, Building building);

	List<Classroom> findByBuilding(Building building);

}
