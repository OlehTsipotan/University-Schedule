package com.university.schedule.converter;

import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.model.Classroom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClassroomDTOToClassroomEntityConverterTest {

    private ClassroomDTOToClassroomEntityConverter classroomDTOToClassroomEntityConverter;

    @BeforeEach
    public void setUp() {
        classroomDTOToClassroomEntityConverter = new ClassroomDTOToClassroomEntityConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenClassroomDTOIsNull_thenIllegalArgumentException(ClassroomDTO nullClassroomDTO) {
        assertThrows(IllegalArgumentException.class,
            () -> classroomDTOToClassroomEntityConverter.convert(nullClassroomDTO));
    }

    @Test
    public void convert_whenClassroomDTOBuildingDTOIsNull_thenIllegalArgumentException() {
        String name = "name";
        ClassroomDTO classroomDTO = ClassroomDTO.builder().id(1L).name(name).buildingDTO(null).build();
        Classroom classroom = Classroom.builder().id(1L).name(name).building(null).build();

        assertEquals(classroom, classroomDTOToClassroomEntityConverter.convert(classroomDTO));
    }

    @Test
    public void convert_whenClassroomDTOIsValid_success() {
        String name = "name";
        ClassroomDTO classroomDTO = ClassroomDTO.builder().id(1L).name(name).build();
        Classroom classroom = Classroom.builder().id(1L).name(name).build();

        assertEquals(classroom, classroomDTOToClassroomEntityConverter.convert(classroomDTO));
    }
}
