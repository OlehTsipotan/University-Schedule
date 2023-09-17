package com.university.schedule.service;

import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.model.ScheduledClass;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ScheduledClassService {

	Long save(ScheduledClass ScheduledClass);

	Long save(ScheduledClassDTO ScheduledClassDTO);

	ScheduledClassDTO findByIdAsDTO(Long id);

	List<ScheduledClass> findAll();

	List<ScheduledClassDTO> findAllAsDTO();

	List<ScheduledClassDTO> findAllAsDTO(Pageable pageable);

	void deleteById(Long id);
}
