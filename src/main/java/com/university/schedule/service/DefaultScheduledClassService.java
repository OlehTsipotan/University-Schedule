package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.ScheduledClass;
import com.university.schedule.repository.ScheduledClassRepository;
import com.university.schedule.validation.ScheduledClassEntityValidator;
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
public class DefaultScheduledClassService implements ScheduledClassService {

	private final ScheduledClassRepository scheduledClassRepository;

	private final ConverterService converterService;

	private final ScheduledClassEntityValidator scheduledClassEntityValidator;

	@Override
	@Transactional
	public Long save(ScheduledClass scheduledClass) {
		execute(() -> {
			scheduledClassEntityValidator.validate(scheduledClass);
			scheduledClassRepository.save(scheduledClass);
		});
		log.info("saved {}", scheduledClass);
		return scheduledClass.getId();
	}

	@Override
	@Transactional
	public Long save(ScheduledClassDTO scheduledClassDTO) {
		ScheduledClass scheduledClass = convertToEntity(scheduledClassDTO);
		execute(() -> {
			scheduledClassEntityValidator.validate(scheduledClass);
			scheduledClassRepository.save(scheduledClass);
		});
		log.info("saved {}", scheduledClass);
		return scheduledClass.getId();
	}

	private ScheduledClass findById(Long id) {
		ScheduledClass scheduledClass = execute(() -> scheduledClassRepository.findById(id)).orElseThrow(
				() -> new ServiceException("ScheduledClass not found"));
		log.debug("Retrieved {}", scheduledClass);
		return scheduledClass;
	}

	@Override
	public ScheduledClassDTO findByIdAsDTO(Long id) {
		ScheduledClass scheduledClass = execute(() -> scheduledClassRepository.findById(id)).orElseThrow(
				() -> new ServiceException("ScheduledClass not found"));
		log.debug("Retrieved {}", scheduledClass);
		return convertToDTO(scheduledClass);
	}

	@Override
	public List<ScheduledClass> findAll() {
		List<ScheduledClass> scheduledClasses = execute(() -> scheduledClassRepository.findAll());
		log.debug("Retrieved All {} ScheduledClasses", scheduledClasses.size());
		return scheduledClasses;
	}

	@Override
	public List<ScheduledClassDTO> findAllAsDTO() {
		List<ScheduledClassDTO> scheduledClassDTOList =
				execute(() -> scheduledClassRepository.findAll()).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} ScheduledClasses", scheduledClassDTOList.size());
		return scheduledClassDTOList;
	}

	@Override
	public List<ScheduledClassDTO> findAllAsDTO(Pageable pageable) {
		List<ScheduledClassDTO> scheduledClassDTOList =
				execute(() -> scheduledClassRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} ScheduledClasses", scheduledClassDTOList.size());
		return scheduledClassDTOList;
	}


	private ScheduledClassDTO convertToDTO(ScheduledClass source) {
		return converterService.convert(source, ScheduledClassDTO.class);
	}

	private ScheduledClass convertToEntity(ScheduledClassDTO source) {
		return converterService.convert(source, ScheduledClass.class);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		try {
			findById(id);
		} catch (ServiceException e) {
			throw new DeletionFailedException("There is no ScheduledClass to delete with id = " + id);
		}
		execute(() -> scheduledClassRepository.deleteById(id));
		log.info("Deleted id = {}", id);
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
