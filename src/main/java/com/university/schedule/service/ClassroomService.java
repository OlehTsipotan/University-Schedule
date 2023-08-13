package com.university.schedule.service;

import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ClassroomService {

    Long save(Classroom classroom);

    Classroom findById(Long id);

    List<Classroom> findByBuilding(Building building);

    List<Classroom> findAll();

    List<Classroom> findAll(Sort sort);

    void deleteById(Long id);
}
