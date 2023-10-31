package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Student;
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

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class StudentEntityValidatorTest {

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();
    private StudentEntityValidator validator;
    @Mock
    private UserEntityValidator userEntityValidator;

    @BeforeEach
    public void setUp() {
        validator = new StudentEntityValidator(userEntityValidator, jakartaValidator);
    }

    @Test
    public void validate_whenStudentIsValid() {
        Student student = new Student("email", "password", "name", "surname");
        assertDoesNotThrow(() -> validator.validate(student));

        verify(userEntityValidator).validate(student);
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenStudentIsNull_throwIllegalArgumentException(Student nullStudent) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullStudent));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void validate_whenStudentNameFieldIsEmptyOrNull_throwValidationException(String name) {
        Student student = new Student("email", "password", name, "surname");
        assertThrows(ValidationException.class, () -> validator.validate(student));

        verify(userEntityValidator).validate(student);
    }
}
