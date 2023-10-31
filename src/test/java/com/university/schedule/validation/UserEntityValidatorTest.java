package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.User;
import com.university.schedule.repository.UserRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserEntityValidatorTest {

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();
    private UserEntityValidator validator;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        validator = new UserEntityValidator(userRepository, jakartaValidator);
    }

    @Test
    public void validate_whenUserIsValid() {
        User user = new User("email", "password", "name", "surname");
        assertDoesNotThrow(() -> validator.validate(user));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenUserIsNull_throwIllegalArgumentException(User nullUser) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullUser));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenUserEmailFieldIsEmptyOrNull_throwValidationException(String email) {
        User user = new User(email, "password", "name", "surname");
        assertThrows(ValidationException.class, () -> validator.validate(user));
    }

    @Test
    public void validate_whenUserEmailIsNotUnique_throwValidationException() {
        String sharedEmail = "email";
        User userToCheck = new User(sharedEmail, "password", "name", "surname");
        User userToBeFounded = new User(sharedEmail, "password", "name", "surname");
        when(userRepository.findByEmail(sharedEmail)).thenReturn(Optional.of(userToBeFounded));
        assertThrows(ValidationException.class, () -> validator.validate(userToCheck));
    }
}
