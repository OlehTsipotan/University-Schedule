package com.university.schedule.repository;

import com.university.schedule.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    Building save(Building building);

    Optional<Building> findById(Long id);

    Optional<Building> findByName(String name);

    Optional<Building> findByAddress(String name);

    List<Building> findAll();

    void deleteById(Long id);
}

