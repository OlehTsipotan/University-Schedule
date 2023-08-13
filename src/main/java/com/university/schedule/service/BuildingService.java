package com.university.schedule.service;

import com.university.schedule.model.Building;

import java.util.List;

public interface BuildingService {

    Long save(Building building);

    Building findById(Long id);

    Building findByName(String name);

    Building findByAddress(String address);

    List<Building> findAll();

    void deleteById(Long id);
}
