package com.university.schedule.converter;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.model.Discipline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DisciplineEntityToDisciplineDTOConverterTest {

    private DisciplineEntityToDisciplineDTOConverter disciplineEntityToDisciplineDTOConverter;

    @BeforeEach
    public void setUp() {
        disciplineEntityToDisciplineDTOConverter = new DisciplineEntityToDisciplineDTOConverter();
    }

    @Test
    public void convert_whenDisciplineIsValid_success() {
        DisciplineDTO disciplineDTO = new DisciplineDTO(1L, "name");
        Discipline discipline = new Discipline(1L, "name");

        assertEquals(disciplineDTO, disciplineEntityToDisciplineDTOConverter.convert(discipline));
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenDisciplineIsNull_throwIllegalArgumentException(Discipline nullDiscipline) {
        assertThrows(IllegalArgumentException.class,
            () -> disciplineEntityToDisciplineDTOConverter.convert(nullDiscipline));
    }
}
