package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Discipline;
import com.university.schedule.repository.DisciplineRepository;
import com.university.schedule.validation.DisciplineEntityValidator;
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
public class DefaultDisciplineService implements DisciplineService {

    private final DisciplineRepository disciplineRepository;

    private final DisciplineEntityValidator disciplineEntityValidator;

    private final ConverterService converterService;

    @Override
    public List<Discipline> findAll() {
        List<Discipline> disciplines = execute(() -> disciplineRepository.findAll());
        log.debug("Retrieved All {} Discipline", disciplines.size());
        return disciplines;
    }

    @Override
    public List<DisciplineDTO> findAllAsDTO() {
        List<DisciplineDTO> disciplineDTOList =
            execute(() -> disciplineRepository.findAll()).stream().map(this::convertToDTO).toList();
        log.debug("Retrieved All {} Discipline", disciplineDTOList.size());
        return disciplineDTOList;
    }

    @Override
    public List<DisciplineDTO> findAllAsDTO(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable is null");
        }
        List<DisciplineDTO> disciplineDTOList =
            execute(() -> disciplineRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
        log.debug("Retrieved All {} Discipline", disciplineDTOList.size());
        return disciplineDTOList;
    }

    @Override
    @Transactional
    public Long save(Discipline discipline) {
        execute(() -> {
            disciplineEntityValidator.validate(discipline);
            disciplineRepository.save(discipline);
        });
        log.info("saved {}", discipline);
        return discipline.getId();
    }

    @Override
    @Transactional
    public Long save(DisciplineDTO disciplineDTO) {
        Discipline discipline = convertToEntity(disciplineDTO);
        execute(() -> {
            disciplineEntityValidator.validate(discipline);
            disciplineRepository.save(discipline);
        });
        log.info("saved {}", discipline);
        return discipline.getId();
    }

    @Override
    public DisciplineDTO findByIdAsDTO(Long id) {
        Discipline discipline = execute(() -> disciplineRepository.findById(id)).orElseThrow(
            () -> new ServiceException("Discipline not found"));
        log.debug("Retrieved {}", discipline);
        return convertToDTO(discipline);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        execute(() -> {
            if (!disciplineRepository.existsById(id)) {
                throw new DeletionFailedException("There is no Discipline to delete with id = " + id);
            }
            disciplineRepository.deleteById(id);
        });
        log.info("Deleted id = {}", id);
    }

    private DisciplineDTO convertToDTO(Discipline discipline) {
        return converterService.convert(discipline, DisciplineDTO.class);
    }

    private Discipline convertToEntity(DisciplineDTO disciplineDTO) {
        return converterService.convert(disciplineDTO, Discipline.class);
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
