package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.ClassTime;
import com.university.schedule.repository.ClassTimeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MyClassTimeService implements ClassTimeService {

    private final ClassTimeRepository classTimeRepository;

    private final Logger logger = LoggerFactory.getLogger(MyClassTimeService.class);

    public MyClassTimeService(ClassTimeRepository classTimeRepository) {
        this.classTimeRepository = classTimeRepository;
    }

    @Override
    public Long save(ClassTime classTime) {
        execute(() -> classTimeRepository.save(classTime));
        logger.info("saved {}", classTime);
        return classTime.getId();
    }

    @Override
    public ClassTime findById(Long id) {
        ClassTime classTime = execute(() -> classTimeRepository.findById(id)).orElseThrow(() -> new ServiceException("ClassTime not found"));
        logger.debug("Retrieved {}", classTime);
        return classTime;
    }

    @Override
    public ClassTime findByOrderNumber(Integer order) {
        ClassTime classTime = execute(() -> classTimeRepository.findByOrderNumber(order)).orElseThrow(() -> new ServiceException("ClassTime not found"));
        logger.debug("Retrieved {}", classTime);
        return classTime;
    }

    @Override
    public List<ClassTime> findAll() {
        List<ClassTime> classTimes = execute(() -> classTimeRepository.findAll());
        logger.debug("Retrieved All {} Groups", classTimes.size());
        return classTimes;
    }

    @Override
    public void deleteById(Long id) {
        execute(() -> classTimeRepository.deleteById(id));
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
