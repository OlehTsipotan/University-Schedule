package com.university.schedule.converter;

import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.model.ScheduledClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScheduledClassDTOToScheduledClassEntityConverterTest {

    private ScheduledClassDTOToScheduledClassEntityConverter scheduledClassDTOToScheduledClassEntityConverter;

    @BeforeEach
    public void setUp() {
        scheduledClassDTOToScheduledClassEntityConverter = new ScheduledClassDTOToScheduledClassEntityConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenScheduledClassDTOIsNull_thenThrowIllegalArgumentException(
        ScheduledClassDTO nullscheduledClassDTO) {
        assertThrows(IllegalArgumentException.class,
            () -> scheduledClassDTOToScheduledClassEntityConverter.convert(nullscheduledClassDTO));
    }

    @Test
    public void convert_whenScheduledClassDTOWithNullFields_success() {
        ScheduledClassDTO scheduledClassDTO = new ScheduledClassDTO();
        ScheduledClass scheduledClass = new ScheduledClass();

        ScheduledClass scheduledClassConverted =
            scheduledClassDTOToScheduledClassEntityConverter.convert(scheduledClassDTO);

        assertEquals(scheduledClass.getId(), scheduledClassConverted.getId());
        assertEquals(scheduledClass.getClassTime(), scheduledClassConverted.getClassTime());
        assertEquals(scheduledClass.getClassType(), scheduledClassConverted.getClassType());
        assertEquals(scheduledClass.getTeacher(), scheduledClassConverted.getTeacher());
        assertEquals(scheduledClass.getGroups(), scheduledClassConverted.getGroups());
        assertEquals(scheduledClass.getCourse(), scheduledClassConverted.getCourse());
        assertEquals(scheduledClass.getClassroom(), scheduledClassConverted.getClassroom());
    }
}
