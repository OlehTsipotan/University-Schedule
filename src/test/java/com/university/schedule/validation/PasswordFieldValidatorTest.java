package com.university.schedule.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class PasswordFieldValidatorTest {

    private PasswordFieldValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new PasswordFieldValidator();
    }

    @Test
    public void isValid_whenPasswordIsValid() {
        String password = "somePassword123";
        assertTrue(validator.isValid(password, null));
    }

    @Test
    public void isValid_whenPasswordIsInvalid() {
        String password = "somepassword";
        assertFalse(validator.isValid(password, null));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void isValid_whenPasswordIsNullOrEmpty_returnFalse(String password) {
        assertFalse(validator.isValid(password, null));
    }

    @Test
    public void isValid_whenPasswordIsTooShort_returnFalse() {
        String password = "123";
        assertFalse(validator.isValid(password, null));
    }

    @Test
    public void isValid_whenPasswordInNotLatin_returnFalse() {
        String password = "Пароль12";
        assertFalse(validator.isValid(password, null));
    }

    @Test
    public void isValid_whenPasswordInNotDigit_returnFalse() {
        String password = "Password";
        assertFalse(validator.isValid(password, null));
    }
}
