package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.ClassType;
import com.university.schedule.repository.ClassTypeRepository;
import com.university.schedule.validation.ClassTypeEntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DefaultClassTypeService implements ClassTypeService {

	private final ClassTypeRepository classTypeRepository;

	private final ConverterService converterService;

	private final ClassTypeEntityValidator classTypeEntityValidator;


	@Override
	@Transactional
	public Long save(ClassTypeDTO classTypeDTO) {
		ClassType classType = convertToEntity(classTypeDTO);
		execute(() -> {
			classTypeEntityValidator.validate(classType);
			classTypeRepository.save(classType);
		});
		log.info("saved {}", classType);
		return classType.getId();
	}

	@Override
	@Transactional
	public Long save(ClassType classType) {
		execute(() -> {
			classTypeEntityValidator.validate(classType);
			classTypeRepository.save(classType);
		});
		log.info("saved {}", classType);
		return classType.getId();
	}

	@Override
	public ClassTypeDTO findByIdAsDTO(Long id) {
		ClassType classType = execute(() -> classTypeRepository.findById(id)).orElseThrow(
				() -> new ServiceException("ClassType not found"));
		log.debug("Retrieved {}", classType);
		return convertToDTO(classType);
	}

	private ClassType findById(Long id) {
		ClassType classType = execute(() -> classTypeRepository.findById(id)).orElseThrow(
				() -> new ServiceException("ClassType not found"));
		log.debug("Retrieved {}", classType);
		return classType;
	}

	@Override
	public ClassType findByName(String name) {
		ClassType classType = execute(() -> classTypeRepository.findByName(name)).orElseThrow(
				() -> new ServiceException("ClassType not found"));
		log.debug("Retrieved {}", classType);
		return classType;
	}

	@Override
	public List<ClassTypeDTO> findAllAsDTO() {
		List<ClassTypeDTO> classTypeDTOList =
				execute(() -> classTypeRepository.findAll()).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} ClassTypes", classTypeDTOList.size());
		return classTypeDTOList;
	}


	@Override
	public List<ClassTypeDTO> findAllAsDTO(Pageable pageable) {
		List<ClassTypeDTO> classTypeDTOList =
				execute(() -> classTypeRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} ClassTypes", classTypeDTOList.size());
		return classTypeDTOList;
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		try {
			findById(id);
		} catch (ServiceException e) {
			throw new DeletionFailedException("There is no ClassType to delete with id = " + id);
		}
		execute(() -> classTypeRepository.deleteById(id));
		log.info("Deleted id = {}", id);
	}


	private ClassTypeDTO convertToDTO(ClassType classType) {
		return converterService.convert(classType, ClassTypeDTO.class);
	}

	private ClassType convertToEntity(ClassTypeDTO classTypeDTO) {
		return converterService.convert(classTypeDTO, ClassType.class);
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
