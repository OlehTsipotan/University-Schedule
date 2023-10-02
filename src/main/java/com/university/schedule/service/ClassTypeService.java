package com.university.schedule.service;

import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.model.ClassType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassTypeService {

	Long save(ClassType classType);

	Long save(ClassTypeDTO classTypeDTO);

	ClassTypeDTO findByIdAsDTO(Long id);

	ClassType findByName(String name);

	List<ClassTypeDTO> findAllAsDTO();

	List<ClassTypeDTO> findAllAsDTO(Pageable pageable);

	void deleteById(Long id);
}
