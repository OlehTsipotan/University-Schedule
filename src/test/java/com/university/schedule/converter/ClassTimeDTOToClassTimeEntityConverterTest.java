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

public class ClassTimeDTOToClassTimeEntityConverterTest {

    private ClassTimeDTOToClassTimeEntityConverter classTimeDTOToClassTimeEntityConverter;

    @BeforeEach
    public void setUp() {
        classTimeDTOToClassTimeEntityConverter = new ClassTimeDTOToClassTimeEntityConverter();
    }

    @Test
    public void convert_whenClassTimeDTOIsValid_success() {
        ClassTimeDTO classTimeDTO =
            ClassTimeDTO.builder().id(1L).orderNumber(1).startTime(LocalTime.of(8, 30)).durationMinutes(90).build();
        ClassTime classTime =
            ClassTime.builder().id(1L).orderNumber(1).startTime(LocalTime.of(8, 30)).duration(Duration.ofMinutes(90))
                .build();

        assertEquals(classTime, classTimeDTOToClassTimeEntityConverter.convert(classTimeDTO));
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenClassTimeDTOIsNull_throwIllegalArgumentException(ClassTimeDTO classTimeDTO) {
        assertThrows(IllegalArgumentException.class,
            () -> classTimeDTOToClassTimeEntityConverter.convert(classTimeDTO));
    }

    @Test
    public void convert_whenClassTimeDTOHasNullOrderNumber_success() {
        ClassTimeDTO classTimeDTO =
            ClassTimeDTO.builder().id(1L).orderNumber(null).startTime(LocalTime.of(8, 30)).durationMinutes(90).build();
        ClassTime classTime =
            ClassTime.builder().id(1L).orderNumber(null).startTime(LocalTime.of(8, 30)).duration(Duration.ofMinutes(90))
                .build();
        assertEquals(classTime, classTimeDTOToClassTimeEntityConverter.convert(classTimeDTO));
    }

    @Test
    public void convert_whenClassTimeDTOFieldsAreNull_success() {
        ClassTimeDTO classTimeDTO = ClassTimeDTO.builder().build();
        ClassTime classTime = ClassTime.builder().build();

        ClassTime classTimeEntity = classTimeDTOToClassTimeEntityConverter.convert(classTimeDTO);

        assertEquals(classTime.getId(), classTimeEntity.getId());
        assertEquals(classTime.getStartTime(), classTimeEntity.getStartTime());
        assertEquals(classTime.getDuration(), classTimeEntity.getDuration());
        assertEquals(classTime.getOrderNumber(), classTimeEntity.getOrderNumber());
    }
}
