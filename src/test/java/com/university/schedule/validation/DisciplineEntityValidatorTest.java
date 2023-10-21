package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Discipline;
import com.university.schedule.repository.DisciplineRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class DisciplineEntityValidatorTest {

    private DisciplineEntityValidator validator;

    @Mock
    private DisciplineRepository disciplineRepository;

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setUp() {
        validator = new DisciplineEntityValidator(disciplineRepository, jakartaValidator);
    }

    @Test
    public void validate_whenDisciplineIsValid() {
        Discipline discipline = new Discipline("name");
        assertDoesNotThrow(() -> validator.validate(discipline));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenDisciplineIsNull_throwIllegalArgumentException(Discipline nullDiscipline) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullDiscipline));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void validate_whenDisciplineNameFieldIsEmptyOrNull_throwValidationException(String name) {
        Discipline discipline = new Discipline(name);
        assertThrows(ValidationException.class, () -> validator.validate(discipline));
    }

    @Test
    public void validate_whenDisciplineNameIsNotUnique_throwValidationException() {
        String sharedName = "name";
        Discipline disciplineToCheck = new Discipline(sharedName);
        Discipline disciplineToBeFounded = new Discipline(sharedName);
        when(disciplineRepository.findByName(sharedName)).thenReturn(Optional.of(disciplineToBeFounded));
        assertThrows(ValidationException.class, () -> validator.validate(disciplineToCheck));
    }

}
