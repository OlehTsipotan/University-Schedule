package com.university.schedule.converter;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.model.ClassTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.time.Duration;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ClassTimeEntityToClassTimeDTOConverterTest {

    private ClassTimeEntityToClassTimeDTOConverter classTimeEntityToClassTimeDTOConverter;

    @BeforeEach
    public void setUp() {
        classTimeEntityToClassTimeDTOConverter = new ClassTimeEntityToClassTimeDTOConverter();
    }

    @Test
    public void convert_whenClassTimeIsValid_success() {
        ClassTimeDTO classTimeDTO =
            ClassTimeDTO.builder().id(1L).orderNumber(1).startTime(LocalTime.of(8, 30)).durationMinutes(90).build();
        ClassTime classTime =
            ClassTime.builder().id(1L).orderNumber(1).startTime(LocalTime.of(8, 30)).duration(Duration.ofMinutes(90))
                .build();

        assertEquals(classTimeDTO, classTimeEntityToClassTimeDTOConverter.convert(classTime));
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenClassTimeIsNull_throwIllegalArgumentException(ClassTime classTime) {
        assertThrows(IllegalArgumentException.class,
            () -> classTimeEntityToClassTimeDTOConverter.convert(classTime));
    }

    @Test
    public void convert_whenClassTimeDTOFieldsAreNull_success() {
        ClassTimeDTO classTimeDTO = ClassTimeDTO.builder().build();
        ClassTime classTime = ClassTime.builder().build();

        ClassTimeDTO classTimeConverted = classTimeEntityToClassTimeDTOConverter.convert(classTime);

        assertEquals(classTimeDTO.getId(), classTimeConverted.getId());
        assertEquals(classTimeDTO.getStartTime(), classTimeConverted.getStartTime());
        assertEquals(classTimeDTO.getDurationMinutes(), classTimeConverted.getDurationMinutes());
        assertEquals(classTimeDTO.getOrderNumber(), classTimeConverted.getOrderNumber());
    }
}

