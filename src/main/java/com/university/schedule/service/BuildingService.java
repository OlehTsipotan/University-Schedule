package com.university.schedule.service;

import com.university.schedule.model.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface BuildingService {

    Long save(Building building);

    Building findById(Long id);

    Building findByName(String name);

    Building findByAddress(String address);

    List<Building> findAll();

    Page<Building> findAll(Pageable pageable);

    List<Building> findAll(Sort sort);

    void deleteById(Long id);
}
