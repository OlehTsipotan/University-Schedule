package com.university.schedule.repository;

import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    List<Classroom> findByBuilding(Building building);

}
