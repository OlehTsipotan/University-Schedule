package com.university.schedule.converter;


import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.model.Building;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BuildingEntityToBuildingDTOConverterTest {

    private BuildingEntityToBuildingDTOConverter buildingEntityToBuildingDTOConverter;

    @BeforeEach
    public void setUp() {
        buildingEntityToBuildingDTOConverter = new BuildingEntityToBuildingDTOConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenBuildingIsNull_throwIllegalArgumentException(Building nullBuilding) {
        assertThrows(IllegalArgumentException.class, () -> buildingEntityToBuildingDTOConverter.convert(nullBuilding));
    }

    @Test
    public void convert_whenBuildingDTOIsValid_success() {
        BuildingDTO buildingDTO = BuildingDTO.builder().id(1L).name("name").address("address").build();
        Building building = Building.builder().id(1L).name("name").address("address").build();
        assertEquals(buildingDTO, buildingEntityToBuildingDTOConverter.convert(building));
    }
}
