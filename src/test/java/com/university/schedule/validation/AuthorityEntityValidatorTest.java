package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Authority;
import com.university.schedule.repository.AuthorityRepository;
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
public class AuthorityEntityValidatorTest {

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();
    private AuthorityEntityValidator validator;
    @Mock
    private AuthorityRepository authorityRepository;

    @BeforeEach
    public void setUp() {
        validator = new AuthorityEntityValidator(authorityRepository, jakartaValidator);
    }

    @Test
    public void validate_whenAuthorityIsValid() {
        Authority authority = Authority.builder().id(1L).name("name").build();
        assertDoesNotThrow(() -> validator.validate(authority));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenAuthorityIsNull_throwIllegalArgumentException(Authority nullAuthority) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullAuthority));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void validate_whenAuthorityNameFieldIsEmptyOrNull_throwValidationException(String name) {
        Authority authority = Authority.builder().id(1L).name(name).build();
        assertThrows(ValidationException.class, () -> validator.validate(authority));
    }

    @Test
    public void validate_whenAuthorityNameIsNotUnique_throwValidationException() {
        String sharedName = "name";

        Authority authorityToValidate = Authority.builder().id(1L).name(sharedName).build();
        Authority authorityToBeFounded = Authority.builder().id(2L).name(sharedName).build();
        when(authorityRepository.findByName(sharedName)).thenReturn(Optional.of(authorityToBeFounded));
        assertThrows(ValidationException.class, () -> validator.validate(authorityToValidate));
    }
}
