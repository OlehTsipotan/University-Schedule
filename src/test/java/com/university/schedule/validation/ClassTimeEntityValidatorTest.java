package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ClassTime;
import com.university.schedule.repository.ClassTimeRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ClassTimeEntityValidatorTest {

    private ClassTimeEntityValidator validator;

    @Mock
    private ClassTimeRepository classTimeRepository;

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setUp() {
        validator = new ClassTimeEntityValidator(classTimeRepository, jakartaValidator);
    }

    @Test
    public void validate_whenClassTimeIsValid() {
        ClassTime classTime = new ClassTime(1, LocalTime.of(8, 0), Duration.ofMinutes(90));
        assertDoesNotThrow(() -> validator.validate(classTime));
    }

    @Test
    public void validate_whenClassTimeIsNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(null));
    }

    @Test
    public void validate_whenClassTimeFieldsAreNull_throwValidationException() {
        ClassTime classTime = new ClassTime();
        assertThrows(ValidationException.class, () -> validator.validate(classTime));
    }

    @Test
    public void validate_whenClassTimeOrderNumberIsNotUnique_throwValidationException() {
        ClassTime classTime = new ClassTime(1, LocalTime.of(8, 0), Duration.ofMinutes(90));
        ClassTime classTimeToBeFounded = new ClassTime(1, LocalTime.of(8, 0), Duration.ofMinutes(90));
        when(classTimeRepository.findByOrderNumber(classTime.getOrderNumber())).thenReturn(
            Optional.of(classTimeToBeFounded));
        assertThrows(ValidationException.class, () -> validator.validate(classTime));
    }
}
