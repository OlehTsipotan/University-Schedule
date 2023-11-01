package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Building;
import com.university.schedule.repository.BuildingRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BuildingEntityValidator extends EntityValidator<Building> {

    private final BuildingRepository buildingRepository;

    public BuildingEntityValidator(BuildingRepository buildingRepository, Validator validator) {
        super(validator);
        this.buildingRepository = buildingRepository;
    }

    @Override
    public void validate(Building building) {
        List<String> violations = new ArrayList<>();
        try {
            super.validate(building);
        } catch (ValidationException e) {
            violations = e.getViolations();
        }

        Optional<Building> buildingToCheck;
        buildingToCheck = buildingRepository.findByName(building.getName());
        if (buildingToCheck.isPresent() && !building.equals(buildingToCheck.get())) {
            violations.add(String.format("Building with name = %s, already exists.", building.getName()));
        }

        buildingToCheck = buildingRepository.findByAddress(building.getAddress());
        if (buildingToCheck.isPresent() && !building.equals(buildingToCheck.get())) {
            violations.add(String.format("Building with address = %s, already exists.", building.getAddress()));
        }

        if (!violations.isEmpty()) {
            throw new ValidationException("Building is not valid", violations);
        }

    }
}

