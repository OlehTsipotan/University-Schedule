package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ScheduleFilterItem;
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

    private final ScheduleFilterItemService scheduleFilterItemService;

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
    public List<ScheduledClassDTO> findAllAsDTOByScheduleFilterItem(ScheduleFilterItem scheduleFilterItem) {
        scheduleFilterItemService.processRawItem(scheduleFilterItem);
        List<ScheduledClassDTO> scheduledClassDTOList;
        if (scheduleFilterItem.getGroupIdList() == null) {
            scheduledClassDTOList = execute(
                () -> scheduledClassRepository.findAllFiltered(scheduleFilterItem.getStartDate(),
                    scheduleFilterItem.getEndDate(), scheduleFilterItem.getClassTypeId(),
                    scheduleFilterItem.getTeacherId())).stream().map(this::convertToDTO).toList();
        } else {
            scheduledClassDTOList = execute(
                () -> scheduledClassRepository.findAllFiltered(scheduleFilterItem.getStartDate(),
                    scheduleFilterItem.getEndDate(), scheduleFilterItem.getClassTypeId(),
                    scheduleFilterItem.getTeacherId(), scheduleFilterItem.getGroupIdList())).stream()
                .map(this::convertToDTO).toList();
        }
        log.debug("Retrieved All {} ScheduledClasses", scheduledClassDTOList.size());
        return scheduledClassDTOList;
    }


    @Override
    public List<ScheduledClassDTO> findAllAsDTO(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable is null");
        }
        List<ScheduledClassDTO> scheduledClassDTOList =
            execute(() -> scheduledClassRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
        log.debug("Retrieved All {} ScheduledClasses", scheduledClassDTOList.size());
        return scheduledClassDTOList;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        execute(() -> {
            if (!scheduledClassRepository.existsById(id)) {
                throw new DeletionFailedException("There is no ScheduledClass to delete with id = " + id);
            }
            scheduledClassRepository.deleteById(id);
        });
        log.info("Deleted id = {}", id);
    }

    private ScheduledClassDTO convertToDTO(ScheduledClass source) {
        return converterService.convert(source, ScheduledClassDTO.class);
    }

    private ScheduledClass convertToEntity(ScheduledClassDTO source) {
        return converterService.convert(source, ScheduledClass.class);
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
