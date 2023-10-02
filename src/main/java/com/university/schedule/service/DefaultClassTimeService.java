package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.ClassTime;
import com.university.schedule.repository.ClassTimeRepository;
import com.university.schedule.validation.ClassTimeEntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class DefaultClassTimeService implements ClassTimeService {

	private final ClassTimeRepository classTimeRepository;

	private final ClassTimeEntityValidator classTimeValidationService;

	private final ConverterService converterService;

	@Override
	@Transactional
	public Long save(ClassTime classTime) {
		execute(() -> {
			classTimeValidationService.validate(classTime);
			classTimeRepository.save(classTime);
		});
		log.info("saved {}", classTime);
		return classTime.getId();
	}

	@Override
	@Transactional
	public Long save(ClassTimeDTO classTimeDTO) {
		ClassTime classTime = convertToEntity(classTimeDTO);
		execute(() -> {
			classTimeValidationService.validate(classTime);
			classTimeRepository.save(classTime);
		});
		log.info("saved {}", classTime);
		return classTime.getId();
	}

	private ClassTime findById(Long id) {
		ClassTime classTime = execute(() -> classTimeRepository.findById(id)).orElseThrow(
				() -> new ServiceException("ClassTime not found"));
		log.debug("Retrieved {}", classTime);
		return classTime;
	}

	@Override
	public ClassTimeDTO findByIdAsDTO(Long id) {
		ClassTime classTime = execute(() -> classTimeRepository.findById(id)).orElseThrow(
				() -> new ServiceException("ClassTime not found"));
		log.debug("Retrieved {}", classTime);
		return convertToDTO(classTime);
	}

	@Override
	public ClassTime findByOrderNumber(Integer order) {
		ClassTime classTime = execute(() -> classTimeRepository.findByOrderNumber(order)).orElseThrow(
				() -> new ServiceException("ClassTime not found"));
		log.debug("Retrieved {}", classTime);
		return classTime;
	}

	private List<ClassTime> findAll() {
		List<ClassTime> classTimes = execute(() -> classTimeRepository.findAll());
		log.debug("Retrieved All {} ClassTimes", classTimes.size());
		return classTimes;
	}


	public List<ClassTimeDTO> findAllAsDTO(Pageable pageable) {
		List<ClassTimeDTO> classTimeDTOList =
				execute(() -> classTimeRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} ClassTimes", classTimeDTOList.size());
		return classTimeDTOList;
	}

	public List<ClassTimeDTO> findAllAsDTO() {
		List<ClassTimeDTO> classTimeDTOList =
				execute(() -> classTimeRepository.findAll()).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} ClassTimes", classTimeDTOList.size());
		return classTimeDTOList;
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		try {
			this.findById(id);
		} catch (ServiceException e) {
			throw new DeletionFailedException("There is no ClassTime to delete with id = " + id);
		}
		execute(() -> classTimeRepository.deleteById(id));
		log.info("Deleted id = {}", id);
	}

	private ClassTimeDTO convertToDTO(ClassTime classTime) {
		return converterService.convert(classTime, ClassTimeDTO.class);
	}

	private ClassTime convertToEntity(ClassTimeDTO classTimeDTO) {
		return converterService.convert(classTimeDTO, ClassTime.class);
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
