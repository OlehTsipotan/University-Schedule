package com.university.schedule.converter;


import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.model.Building;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BuildingDTOToBuildingEntityConverterTest {

    private BuildingDTOToBuildingEntityConverter buildingDTOToBuildingEntityConverter;

    @BeforeEach
    public void setUp() {
        buildingDTOToBuildingEntityConverter = new BuildingDTOToBuildingEntityConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenBuildingDTOIsNull_throwIllegalArgumentException(BuildingDTO nullBuildingDTO) {
        assertThrows(IllegalArgumentException.class,
            () -> buildingDTOToBuildingEntityConverter.convert(nullBuildingDTO));
    }

    @Test
    public void convert_whenBuildingDTOIsValid_success() {
        BuildingDTO buildingDTO = BuildingDTO.builder().id(1L).name("name").address("address").build();
        Building building = Building.builder().id(1L).name("name").address("address").build();
        assertEquals(building, buildingDTOToBuildingEntityConverter.convert(buildingDTO));
    }
}
