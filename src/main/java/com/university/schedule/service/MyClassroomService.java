package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import com.university.schedule.repository.ClassroomRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MyClassroomService implements ClassroomService {

    private final ClassroomRepository classroomRepository;

    private final Logger logger = LoggerFactory.getLogger(MyClassroomService.class);

    public MyClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    @Override
    public Long save(Classroom classroom) {
        execute(() -> classroomRepository.save(classroom));
        logger.info("saved {}", classroom);
        return classroom.getId();
    }

    @Override
    public Classroom findById(Long id) {
        Classroom classroom = execute(() -> classroomRepository.findById(id)).orElseThrow(() -> new ServiceException("Classroom not found"));
        logger.debug("Retrieved {}", classroom);
        return classroom;
    }

    @Override
    public List<Classroom> findByBuilding(Building building) {
        List<Classroom> classrooms = execute(() -> classroomRepository.findByBuilding(building));
        logger.debug("Retrieved All {} Classrooms", classrooms.size());
        return classrooms;
    }

    @Override
    public List<Classroom> findAll() {
        List<Classroom> classrooms = execute(() -> classroomRepository.findAll());
        logger.debug("Retrieved All {} Classrooms", classrooms.size());
        return classrooms;
    }

    @Override
    public void deleteById(Long id) {
        execute(() -> classroomRepository.deleteById(id));
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
