package com.university.schedule.converter;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClassroomEntityToClassroomDTOConverterTest {

    private ClassroomEntityToClassroomDTOConverter classroomEntityToClassroomDTOConverter;

    @BeforeEach
    public void setUp() {
        classroomEntityToClassroomDTOConverter = new ClassroomEntityToClassroomDTOConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenClassroomIsNull_throwIllegalArgumentException(Classroom nullClassroom) {
        assertThrows(IllegalArgumentException.class,
            () -> classroomEntityToClassroomDTOConverter.convert(nullClassroom));
    }

    @Test
    public void convert_whenClassroomBuildingIsNull_throwIllegalArgumentException() {
        Classroom classroom = Classroom.builder().id(1L).name("name").building(null).build();
        ClassroomDTO classroomDTO = ClassroomDTO.builder().id(1L).name("name").buildingDTO(null).build();
        assertEquals(classroomDTO, classroomEntityToClassroomDTOConverter.convert(classroom));
    }

    @Test
    public void convert_whenClassroomIsValid_success() {
        Building building = Building.builder().name("name").address("address").build();
        Classroom classroom =
            Classroom.builder().id(1L).name("name").building(building).build();

        BuildingDTO buildingDTO = BuildingDTO.builder().name("name").address("address").build();
        ClassroomDTO classroomDTO = ClassroomDTO.builder().id(1L).name("name").buildingDTO(buildingDTO).build();

        assertEquals(classroomDTO, classroomEntityToClassroomDTOConverter.convert(classroom));
    }


}
