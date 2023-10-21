package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Role;
import com.university.schedule.repository.RoleRepository;
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
public class RoleEntityValidatorTest {
    private RoleEntityValidator validator;

    @Mock
    private RoleRepository roleRepository;

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setUp() {
        validator = new RoleEntityValidator(roleRepository, jakartaValidator);
    }

    @Test
    public void validate_whenRoleIsValid() {
        Role role = new Role("name");
        assertDoesNotThrow(() -> validator.validate(role));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenRoleIsNull_throwIllegalArgumentException(Role nullRole) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullRole));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void validate_whenRoleNameFieldIsEmptyOrNull_throwValidationException(String name) {
        Role role = new Role(name);
        assertThrows(ValidationException.class, () -> validator.validate(role));
    }

    @Test
    public void validate_whenRoleNameIsNotUnique_throwValidationException() {
        String sharedName = "name";
        Role roleToCheck = new Role(sharedName);
        Role roleToBeFounded = new Role(sharedName);
        when(roleRepository.findByName(sharedName)).thenReturn(Optional.of(roleToBeFounded));
        assertThrows(ValidationException.class, () -> validator.validate(roleToCheck));
    }

}
