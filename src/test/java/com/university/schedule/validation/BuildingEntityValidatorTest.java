package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Building;
import com.university.schedule.repository.BuildingRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class BuildingEntityValidatorTest {


    private BuildingEntityValidator validator;

    @Mock
    private BuildingRepository buildingRepository;

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setUp() {
        validator = new BuildingEntityValidator(buildingRepository, jakartaValidator);
    }


    @Test
    public void validate_whenBuildingIsValid() {
        Building building = Building.builder().id(1L).name("name").address("address").build();
        assertDoesNotThrow(() -> validator.validate(building));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenBuildingIsNull_throwIllegalArgumentException(Building nullBuilding) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullBuilding));
    }

    // It is no matter what field is empty
    @ParameterizedTest
    @NullAndEmptySource
    public void validate_whenBuildingNameFieldIsEmpty_throwValidationException(String name) {
        Building building = Building.builder().id(1L).name(name).address("address").build();
        assertThrows(ValidationException.class, () -> validator.validate(building));
    }

    @Test
    public void validate_whenBuildingNameIsNotUnique_throwValidationException() {

        String shareName = "name";

        Building buildingToCheck = Building.builder().id(1L).name(shareName).address("addressToCheck").build();

        Building buildingToFind = Building.builder().id(2L).name(shareName).address("addressToFind").build();

        when(buildingRepository.findByName(shareName)).thenReturn(Optional.ofNullable(buildingToFind));

        assertThrows(ValidationException.class, () -> validator.validate(buildingToCheck));
    }

    @Test
    public void validate_whenBuildingAddressIsNotUnique_throwValidationException() {

        String shareAddress = "address";

        Building buildingToCheck = Building.builder().id(1L).name("nameToCheck").address(shareAddress).build();

        Building buildingToFind = Building.builder().id(2L).name("nameToFind").address(shareAddress).build();

        when(buildingRepository.findByAddress(shareAddress)).thenReturn(Optional.ofNullable(buildingToFind));

        assertThrows(ValidationException.class, () -> validator.validate(buildingToCheck));
    }


}