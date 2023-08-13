package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.ClassType;
import com.university.schedule.repository.ClassTypeRepository;
import com.university.schedule.utility.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DefaultClassTypeService implements ClassTypeService{

    private final ClassTypeRepository classTypeRepository;

    private final EntityValidator entityValidator;


    @Override
    @Transactional
    public Long save(ClassType classType) {
        entityValidator.validate(classType);
        execute(() -> classTypeRepository.save(classType));
        log.info("saved {}", classType);
        return classType.getId();
    }

    @Override
    public ClassType findById(Long id) {
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
    public List<ClassType> findAll() {
        List<ClassType> classTypes = execute(() -> classTypeRepository.findAll());
        log.debug("Retrieved All {} ClassTypes", classTypes.size());
        return classTypes;
    }

    @Override
    public List<ClassType> findAll(Sort sort) {
        List<ClassType> classTypes = execute(() -> classTypeRepository.findAll(sort));
        log.debug("Retrieved All {} ClassTypes", classTypes.size());
        return classTypes;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        execute(() -> classTypeRepository.deleteById(id));
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
