package com.university.schedule.service;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.model.Discipline;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DisciplineService {

	List<Discipline> findAll();

	List<DisciplineDTO> findAllAsDTO();

	List<DisciplineDTO> findAllAsDTO(Pageable pageable);

	Long save(Discipline discipline);

	Long save(DisciplineDTO disciplineDTO);

	DisciplineDTO findByIdAsDTO(Long id);

	void deleteById(Long id);

}
