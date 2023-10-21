package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ClassType;
import com.university.schedule.repository.ClassTypeRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ClassTypeEntityValidatorTest {

    private ClassTypeEntityValidator validator;

    @Mock
    private ClassTypeRepository classTypeRepository;

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setUp() {
        validator = new ClassTypeEntityValidator(classTypeRepository, jakartaValidator);
    }


    @Test
    public void validate_whenClassTypeIsValid() {
        ClassType classType = new ClassType(1L, "name");
        assertDoesNotThrow(() -> validator.validate(classType));
        verify(classTypeRepository).findByName(classType.getName());
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenClassTypeIsNull_callJakartaValidator(ClassType nullClassType) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullClassType));
    }

    @ParameterizedTest
    @CsvSource({"sharedName"})
    public void validate_whenClassTypeNameIsNotUnique_throwValidationException(String sharedName) {
        ClassType classTypeToCheck = new ClassType(1L, sharedName);
        ClassType classTypeToBeFounded = new ClassType(2L, sharedName);
        when(classTypeRepository.findByName(sharedName)).thenReturn(Optional.of(classTypeToBeFounded));
        assertThrows(ValidationException.class, () -> validator.validate(classTypeToCheck));
    }

    @Test
    public void validate_whenClassTypeNameIsNull_callJakartaValidator() {
        ClassType classType = new ClassType(1L, null);
        assertThrows(ValidationException.class, () -> validator.validate(classType));
    }

}
