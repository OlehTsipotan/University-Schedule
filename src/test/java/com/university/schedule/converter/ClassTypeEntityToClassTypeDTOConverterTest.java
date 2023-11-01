package com.university.schedule.converter;

import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.model.ClassType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClassTypeEntityToClassTypeDTOConverterTest {

    private ClassTypeEntityToClassTypeDTOConverter classTypeEntityToClassTypeDTOConverter;

    @BeforeEach
    public void setUp() {
        classTypeEntityToClassTypeDTOConverter = new ClassTypeEntityToClassTypeDTOConverter();
    }

    @Test
    public void convert_whenClassTypeIsValid_success() {
        ClassTypeDTO classTypeDTO = new ClassTypeDTO(1L, "classType");
        ClassType classType = new ClassType(1L, "classType");
        assertEquals(classTypeDTO, classTypeEntityToClassTypeDTOConverter.convert(classType));
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenClassTypeIsNull_throwIllegalArgumentException(ClassType nullClassType) {
        assertThrows(IllegalArgumentException.class,
            () -> classTypeEntityToClassTypeDTOConverter.convert(nullClassType));
    }
}
