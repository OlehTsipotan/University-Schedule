package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Building;
import com.university.schedule.repository.BuildingRepository;
import com.university.schedule.utility.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefaultBuildingService implements BuildingService{

    private final BuildingRepository buildingRepository;

    private final EntityValidator entityValidator;

    @Override
    @Transactional
    public Long save(Building building) {
        entityValidator.validate(entityValidator);
        execute(() -> buildingRepository.save(building));
        log.info("saved {}", building);
        return building.getId();
    }

    @Override
    public Building findById(Long id) {
        Building building = execute(() -> buildingRepository.findById(id)).orElseThrow(
                () -> new ServiceException("Building not found"));
        log.debug("Retrieved {}", building);
        return building;
    }

    @Override
    public Building findByName(String name) {
        Building building = execute(() -> buildingRepository.findByName(name)).orElseThrow(
                () -> new ServiceException("Building not found"));
        log.debug("Retrieved {}", building);
        return building;
    }

    @Override
    public Building findByAddress(String address) {
        Building building = execute(() -> buildingRepository.findByName(address)).orElseThrow(
                () -> new ServiceException("Building not found"));
        log.debug("Retrieved {}", building);
        return building;
    }

    @Override
    public List<Building> findAll() {
        List<Building> buildings = execute(() -> buildingRepository.findAll());
        log.debug("Retrieved All {} Groups", buildings.size());
        return buildings;
    }

    @Override
    public Page<Building> findAll(Pageable pageable) {
        Page<Building> buildings = execute(() -> buildingRepository.findAll(pageable));
        log.debug("Retrieved All {} Groups", buildings.stream().count());
        return buildings;
    }

    @Override
    public List<Building> findAll(Sort sort) {
        List<Building> buildings = execute(() -> buildingRepository.findAll(sort));
        log.debug("Retrieved All {} Groups", buildings.size());
        return buildings;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try{
            findById(id);
        } catch (ServiceException e){
            throw new ServiceException("There is no Building to delete with id = "+ id);
        }
        execute(() -> buildingRepository.deleteById(id));
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
