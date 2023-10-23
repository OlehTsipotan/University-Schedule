package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Building;
import com.university.schedule.repository.BuildingRepository;
import com.university.schedule.validation.BuildingEntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefaultBuildingService implements BuildingService {

	private final BuildingRepository buildingRepository;

	private final ConverterService converterService;

	private final BuildingEntityValidator buildingValidationService;

	@Override
	@Transactional
	public Long save(Building building) {
		execute(() -> {
			buildingValidationService.validate(building);
			buildingRepository.save(building);
		});
		log.info("saved {}", building);
		return building.getId();
	}

	@Override
	@Transactional
	public Long save(BuildingDTO buildingDTO) {
		Building building = convertToEntity(buildingDTO);
		execute(() -> {
			buildingValidationService.validate(building);
			buildingRepository.save(building);
		});
		log.info("saved {}", building);
		return building.getId();
	}

	@Override
	public BuildingDTO findByIdAsDTO(Long id) {
        Building building = execute(() -> buildingRepository.findById(id)).orElseThrow(
            () -> new ServiceException("Building not found"));
        log.debug("Retrieved {}", building);
        return convertToDTO(building);
	}

	@Override
	public BuildingDTO findByNameAsDTO(String name) {
		Building building = execute(() -> buildingRepository.findByName(name)).orElseThrow(
				() -> new ServiceException("Building not found"));
		log.debug("Retrieved {}", building);
		return convertToDTO(building);
	}

	@Override
	public BuildingDTO findByAddressAsDTO(String address) {
		Building building = execute(() -> buildingRepository.findByAddress(address)).orElseThrow(
				() -> new ServiceException("Building not found"));
		log.debug("Retrieved {}", building);
		return convertToDTO(building);
	}

	@Override
	public List<Building> findAll() {
		List<Building> buildings = execute(() -> buildingRepository.findAll());
		log.debug("Retrieved All {} Buildings", buildings.size());
		return buildings;
	}

	@Override
	public List<BuildingDTO> findAllAsDTO() {
		List<BuildingDTO> buildingDTOList =
				execute(() -> buildingRepository.findAll()).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Buildings", buildingDTOList.size());
		return buildingDTOList;
	}

	@Override
	public List<BuildingDTO> findAllAsDTO(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable is null");
        }
		List<BuildingDTO> buildingDTOList =
				execute(() -> buildingRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Buildings", buildingDTOList.size());
		return buildingDTOList;
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
        execute(() -> {
            if (!buildingRepository.existsById(id)) {
                throw new DeletionFailedException("There is no Building to delete with id = " + id);
            }
            buildingRepository.deleteById(id);
        });
		log.info("Deleted id = {}", id);
	}

	private BuildingDTO convertToDTO(Building building) {
		return converterService.convert(building, BuildingDTO.class);
	}

	private Building convertToEntity(BuildingDTO buildingDTO) {
		return converterService.convert(buildingDTO, Building.class);
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
