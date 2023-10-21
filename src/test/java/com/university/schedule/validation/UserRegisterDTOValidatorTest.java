package com.university.schedule.validation;


import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.UserRegisterDTO;
import com.university.schedule.exception.ValidationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserRegisterDTOValidatorTest {

    private UserRegisterDTOValidator validator;

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setUp() {
        validator = new UserRegisterDTOValidator(jakartaValidator);
    }

    @Test
    public void validate_whenUserIsValid() {
        UserRegisterDTO user = new UserRegisterDTO("email", "firstName", "lastName", "password123", "password123",
            new RoleDTO(1L, "ROLE_STUDENT"));
        assertDoesNotThrow(() -> validator.validate(user));
    }

    @Test
    public void validate_whenPasswordsDontMatch_throwValidationException() {
        UserRegisterDTO user = new UserRegisterDTO("email", "firstName", "lastName", "password123", "password1234",
            new RoleDTO(1L, "ROLE_STUDENT"));
        assertThrows(ValidationException.class, () -> validator.validate(user));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenUserIsNull_throwIllegalArgumentException(UserRegisterDTO nullUser){
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullUser));
    }
}
