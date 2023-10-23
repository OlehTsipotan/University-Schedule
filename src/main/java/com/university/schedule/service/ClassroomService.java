package com.university.schedule.service;

import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassroomService {

    Long save(Classroom classroom);

    Long save(ClassroomDTO classroomDTO);

    ClassroomDTO findByIdAsDTO(Long id);

    List<Classroom> findByBuilding(Building building);

    List<ClassroomDTO> findAllAsDTO();

    List<ClassroomDTO> findAllAsDTO(Pageable pageable);

    void deleteById(Long id);
}
