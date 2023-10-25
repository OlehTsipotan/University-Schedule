package com.university.schedule.converter;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.model.Discipline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DisciplineDTOToDisciplineEntityConverterTest {

    private DisciplineDTOToDisciplineEntityConverter disciplineDTOToDisciplineEntityConverter;

    @BeforeEach
    public void setUp() {
        disciplineDTOToDisciplineEntityConverter = new DisciplineDTOToDisciplineEntityConverter();
    }

    @Test
    public void convert_whenDisciplineIsValid_success() {
        DisciplineDTO disciplineDTO = new DisciplineDTO(1L, "name");
        Discipline discipline = new Discipline(1L, "name");

        assertEquals(discipline, disciplineDTOToDisciplineEntityConverter.convert(disciplineDTO));
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenDisciplineDTOIsNull_throwIllegalArgumentException(DisciplineDTO nullDisciplineDTO) {
        assertThrows(IllegalArgumentException.class,
            () -> disciplineDTOToDisciplineEntityConverter.convert(nullDisciplineDTO));
    }
}
