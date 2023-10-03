package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import com.university.schedule.repository.ClassroomRepository;
import com.university.schedule.validation.ClassroomEntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefaultClassroomService implements ClassroomService {

	private final ClassroomRepository classroomRepository;

	private final ClassroomEntityValidator classroomValidationService;

	private final ConverterService converterService;

	private final BuildingService buildingService;

	@Override
	@Transactional
	public Long save(Classroom classroom) {
		execute(() -> {
			classroomValidationService.validate(classroom);
			classroomRepository.save(classroom);
		});
		log.info("saved {}", classroom);
		return classroom.getId();
	}

	@Override
	@Transactional
	public Long save(ClassroomDTO classroomDTO) {
		Classroom classroom = convertToEntity(classroomDTO);
		execute(() -> {
			classroomValidationService.validate(classroom);
			classroomRepository.save(classroom);
		});
		log.info("saved {}", classroom);
		return classroom.getId();
	}



	@Override
	public ClassroomDTO findByIdAsDTO(Long id) {
		Classroom classroom = execute(() -> classroomRepository.findById(id)).orElseThrow(
				() -> new ServiceException("Classroom not found"));
		log.debug("Retrieved {}", classroom);
		return convertToDTO(classroom);
	}

	private Classroom findById(Long id) {
		Classroom classroom = execute(() -> classroomRepository.findById(id)).orElseThrow(
				() -> new ServiceException("Classroom not found"));
		log.debug("Retrieved {}", classroom);
		return classroom;
	}

	@Override
	public Classroom findByNameAndBuilding(String name, Building building) {
		Classroom classroom = execute(() -> classroomRepository.findByNameAndBuilding(name, building)).orElseThrow(
				() -> new ServiceException("Classroom not found"));
		log.debug("Retrieved {}", classroom);
		return classroom;
	}

	@Override
	public List<Classroom> findByBuilding(Building building) {
		List<Classroom> classrooms = execute(() -> classroomRepository.findByBuilding(building));
		log.debug("Retrieved All {} Classrooms", classrooms.size());
		return classrooms;
	}

	@Override
	public List<ClassroomDTO> findAllAsDTO() {
		List<ClassroomDTO> classroomDTOList =
				execute(() -> classroomRepository.findAll()).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Classrooms", classroomDTOList.size());
		return classroomDTOList;
	}

	@Override
	public List<ClassroomDTO> findAllAsDTO(Pageable pageable) {
		List<ClassroomDTO> classroomDTOList =
				execute(() -> classroomRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Classrooms", classroomDTOList.size());
		return classroomDTOList;
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		try {
			findById(id);
		} catch (ServiceException e) {
			throw new DeletionFailedException("There is no Classroom to delete with id = " + id);
		}
		execute(() -> classroomRepository.deleteById(id));
		log.info("Deleted id = {}", id);
	}

	private ClassroomDTO convertToDTO(Classroom classroom) {
		return converterService.convert(classroom, ClassroomDTO.class);
	}

	private Classroom convertToEntity(ClassroomDTO classroomDTO) {
		return converterService.convert(classroomDTO, Classroom.class);
	}

	private <T> T execute(DaoSupplier<T> supplier) {
		try {
			return supplier.get();
		} catch (DataAccessException e) {
			throw new ServiceException("DAO operation failed", e);
		}
	}

	private void execute(DaoProcessor processor) {
		try {
			processor.process();
		} catch (DataAccessException e) {
			throw new ServiceException("DAO operation failed", e);
		}
	}

	@FunctionalInterface
	public interface DaoSupplier<T> {
		T get();
	}

	@FunctionalInterface
	public interface DaoProcessor {
		void process();
	}
}
