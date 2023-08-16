package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import com.university.schedule.repository.ClassroomRepository;
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
@Service
@Transactional(readOnly = true)
public class DefaultClassroomService implements ClassroomService {

    private final ClassroomRepository classroomRepository;

    private final EntityValidator entityValidator;


    @Override
    @Transactional
    public Long save(Classroom classroom) {
        entityValidator.validate(classroom);
        execute(() -> classroomRepository.save(classroom));
        log.info("saved {}", classroom);
        return classroom.getId();
    }

    @Override
    public Classroom findById(Long id) {
        Classroom classroom = execute(() -> classroomRepository.findById(id)).orElseThrow(
                () -> new ServiceException("Classroom not found"));
        log.debug("Retrieved {}", classroom);
        return classroom;
    }

    @Override
    public List<Classroom> findByBuilding(Building building) {
        List<Classroom> classrooms = execute(() -> classroomRepository.findByBuilding(building));
        log.debug("Retrieved All {} Classrooms", classrooms.size());
        return classrooms;
    }

    @Override
    public List<Classroom> findAll() {
        List<Classroom> classrooms = execute(() -> classroomRepository.findAll());
        log.debug("Retrieved All {} Classrooms", classrooms.size());
        return classrooms;
    }

    @Override
    public List<Classroom> findAll(Sort sort) {
        List<Classroom> classrooms = execute(() -> classroomRepository.findAll(sort));
        log.debug("Retrieved All {} Classrooms", classrooms.size());
        return classrooms;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try{
            findById(id);
        } catch (ServiceException e){
            throw new ServiceException("There is no Classroom to delete with id = "+ id);
        }
        execute(() -> classroomRepository.deleteById(id));
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
