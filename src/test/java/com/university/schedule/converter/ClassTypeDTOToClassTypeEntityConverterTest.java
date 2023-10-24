package com.university.schedule.converter;

import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.model.ClassType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClassTypeDTOToClassTypeEntityConverterTest {

    private ClassTypeDTOToClassTypeEntityConverter classTypeDTOToClassTypeEntityConverter;

    @BeforeEach
    public void setUp() {
        classTypeDTOToClassTypeEntityConverter = new ClassTypeDTOToClassTypeEntityConverter();
    }

    @Test
    public void convert_whenClassTypeDTOIsValid_success() {
        ClassTypeDTO classTypeDTO = new ClassTypeDTO(1L, "classType");
        ClassType classType = new ClassType(1L, "classType");
        assertEquals(classType, classTypeDTOToClassTypeEntityConverter.convert(classTypeDTO));
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenClassTypeDTOIsNull_throwIllegalArgumentException(ClassTypeDTO nullClassTypeDTO) {
        assertThrows(IllegalArgumentException.class,
            () -> classTypeDTOToClassTypeEntityConverter.convert(nullClassTypeDTO));
    }
}
