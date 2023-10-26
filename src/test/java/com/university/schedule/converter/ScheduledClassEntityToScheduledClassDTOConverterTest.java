package com.university.schedule.converter;

import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.model.ScheduledClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduledClassEntityToScheduledClassDTOConverterTest {

    private ScheduledClassEntityToScheduledClassDTOConverter scheduledClassEntityToScheduledClassDTOConverter;

    @BeforeEach
    public void setUp() {
        scheduledClassEntityToScheduledClassDTOConverter = new ScheduledClassEntityToScheduledClassDTOConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenScheduledClassDTOIsNull_thenThrowIllegalArgumentException(
        ScheduledClass nullScheduledClass) {
        assertThrows(IllegalArgumentException.class,
            () -> scheduledClassEntityToScheduledClassDTOConverter.convert(nullScheduledClass));
    }

    @Test
    public void convert_whenScheduledClassDTOWithNullFields_success() {
        ScheduledClassDTO scheduledClassDTO = new ScheduledClassDTO();
        ScheduledClass scheduledClass = new ScheduledClass();

        ScheduledClassDTO scheduledClassDTOConverted =
            scheduledClassEntityToScheduledClassDTOConverter.convert(scheduledClass);

        assertEquals(scheduledClassDTO.getId(), scheduledClassDTOConverted.getId());
        assertEquals(scheduledClassDTO.getClassTimeDTO(), scheduledClassDTOConverted.getClassTimeDTO());
        assertEquals(scheduledClassDTO.getClassTypeDTO(), scheduledClassDTOConverted.getClassTypeDTO());
        assertEquals(scheduledClassDTO.getTeacherDTO(), scheduledClassDTOConverted.getTeacherDTO());
        assertEquals(scheduledClassDTOConverted.getGroupDTOS(), new ArrayList<>());
        assertNull(scheduledClassDTOConverted.getCourseDTO());
        assertEquals(scheduledClassDTO.getClassroomDTO(), scheduledClassDTOConverted.getClassroomDTO());
    }
}
