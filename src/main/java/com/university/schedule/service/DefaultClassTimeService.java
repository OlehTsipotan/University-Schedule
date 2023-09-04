package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.ClassTime;
import com.university.schedule.repository.ClassTimeRepository;
import com.university.schedule.validation.ClassTimeEntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ClassTime findById(Long id) {
        ClassTime classTime = execute(() -> classTimeRepository.findById(id)).orElseThrow(
                () -> new ServiceException("ClassTime not found"));
        log.debug("Retrieved {}", classTime);
        return classTime;
    }

    @Override
    public ClassTime findByOrderNumber(Integer order) {
        ClassTime classTime = execute(() -> classTimeRepository.findByOrderNumber(order)).orElseThrow(
                () -> new ServiceException("ClassTime not found"));
        log.debug("Retrieved {}", classTime);
        return classTime;
    }

    @Override
    public List<ClassTime> findAll() {
        List<ClassTime> classTimes = execute(() -> classTimeRepository.findAll());
        log.debug("Retrieved All {} Groups", classTimes.size());
        return classTimes;
    }

    @Override
    public List<ClassTime> findAll(Sort sort) {
        List<ClassTime> classTimes = execute(() -> classTimeRepository.findAll(sort));
        log.debug("Retrieved All {} Groups", classTimes.size());
        return classTimes;
    }

    @Override
    public Page<ClassTime> findAll(Pageable pageable) {
        Page<ClassTime> classTimes = execute(() -> classTimeRepository.findAll(pageable));
        log.debug("Retrieved All {} Groups", classTimes.getTotalElements());
        return classTimes;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try{
            findById(id);
        } catch (ServiceException e){
            throw new ServiceException("There is no ClassTime to delete with id = "+ id);
        }
        execute(() -> classTimeRepository.deleteById(id));
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
