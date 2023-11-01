package com.university.schedule.service;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.model.Building;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BuildingService {

    Long save(Building building);

    Long save(BuildingDTO buildingDTO);

    BuildingDTO findByIdAsDTO(Long id);

    BuildingDTO findByNameAsDTO(String name);

    BuildingDTO findByAddressAsDTO(String address);

    List<Building> findAll();

    List<BuildingDTO> findAllAsDTO();

    List<BuildingDTO> findAllAsDTO(Pageable pageable);

    void deleteById(Long id);
}
