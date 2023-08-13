package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Discipline;
import com.university.schedule.repository.DisciplineRepository;
import com.university.schedule.utility.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class DefaultDisciplineService implements DisciplineService{

    private final DisciplineRepository disciplineRepository;

    private final EntityValidator entityValidator;

    @Override
    public List<Discipline> findAll() {
        List<Discipline> disciplines = execute(() -> disciplineRepository.findAll());
        log.debug("Retrieved All {} Discipline", disciplines.size());
        return disciplines;
    }

    @Override
    public List<Discipline> findAll(Sort sort) {
        List<Discipline> disciplines = execute(() -> disciplineRepository.findAll(sort));
        log.debug("Retrieved All {} Discipline", disciplines.size());
        return disciplines;
    }

    @Override
    @Transactional
    public Long save(Discipline discipline) {
        entityValidator.validate(discipline);
        execute(() -> disciplineRepository.save(discipline));
        log.info("saved {}", discipline);
        return discipline.getId();
    }

    @Override
    public Discipline findById(Long id) {
        Discipline discipline = execute(() -> disciplineRepository.findById(id)).orElseThrow(
                () -> new ServiceException("Discipline not found"));
        log.debug("Retrieved {}", discipline);
        return discipline;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        execute(() -> disciplineRepository.deleteById(id));
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
