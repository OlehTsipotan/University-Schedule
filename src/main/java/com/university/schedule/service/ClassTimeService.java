package com.university.schedule.service;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.model.ClassTime;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassTimeService {

	Long save(ClassTime classTime);

	Long save(ClassTimeDTO classTimeDTO);

	ClassTimeDTO findByIdAsDTO(Long id);

	ClassTime findByOrderNumber(Integer order);

	List<ClassTimeDTO> findAllAsDTO();

	List<ClassTimeDTO> findAllAsDTO(Pageable pageable);

	void deleteById(Long id);
}
