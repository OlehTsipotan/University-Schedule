package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.ClassTime;
import com.university.schedule.model.ClassType;
import com.university.schedule.repository.ClassTypeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class MyClassTypeService implements ClassTypeService{

    private final ClassTypeRepository classTypeRepository;

    private final Logger logger = LoggerFactory.getLogger(MyClassTimeService.class);

    public MyClassTypeService(ClassTypeRepository classTypeRepository) {
        this.classTypeRepository = classTypeRepository;
    }

    @Override
    public Long save(ClassType classType) {
        execute(() -> classTypeRepository.save(classType));
        logger.info("saved {}", classType);
        return classType.getId();
    }

    @Override
    public ClassType findById(Long id) {
        ClassType classType = execute(() -> classTypeRepository.findById(id)).orElseThrow(() -> new ServiceException("ClassType not found"));
        logger.debug("Retrieved {}", classType);
        return classType;
    }

    @Override
    public ClassType findByName(String name) {
        ClassType classType = execute(() -> classTypeRepository.findByName(name)).orElseThrow(() -> new ServiceException("ClassType not found"));
        logger.debug("Retrieved {}", classType);
        return classType;
    }

    @Override
    public List<ClassType> findAll() {
        List<ClassType> classTypes = execute(() -> classTypeRepository.findAll());
        logger.debug("Retrieved All {} ClassTypes", classTypes.size());
        return classTypes;
    }

    @Override
    public void deleteById(Long id) {
        execute(() -> classTypeRepository.deleteById(id));
        logger.info("Deleted id = {}", id);
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
