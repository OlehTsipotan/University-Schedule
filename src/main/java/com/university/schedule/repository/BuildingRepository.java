package com.university.schedule.repository;

import com.university.schedule.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {
	Optional<Building> findByName(String name);

	Optional<Building> findByAddress(String name);
}

