package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Teacher;
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
public class TeacherEntityValidatorTest {

    private TeacherEntityValidator validator;

    @Mock
    private UserEntityValidator userEntityValidator;

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setUp() {
        validator = new TeacherEntityValidator(userEntityValidator, jakartaValidator);
    }

    @Test
    public void validate_whenTeacherIsValid() {
        Teacher teacher = new Teacher("email", "password", "name", "surname");
        assertDoesNotThrow(() -> validator.validate(teacher));

        verify(userEntityValidator).validate(teacher);
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenTeacherIsNull_throwIllegalArgumentException(Teacher nullTeacher) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullTeacher));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void validate_whenTeacherNameFieldIsEmptyOrNull_throwValidationException(String name) {
        Teacher teacher = new Teacher("email", "password", name, "surname");
        assertThrows(ValidationException.class, () -> validator.validate(teacher));

        verify(userEntityValidator).validate(teacher);
    }
}
