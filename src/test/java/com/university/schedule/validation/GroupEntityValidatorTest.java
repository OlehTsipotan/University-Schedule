package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import com.university.schedule.repository.GroupRepository;
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
public class GroupEntityValidatorTest {

    private GroupEntityValidator validator;

    @Mock
    private GroupRepository groupRepository;

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setUp() {
        validator = new GroupEntityValidator(groupRepository, jakartaValidator);
    }

    @Test
    public void validate_whenGroupIsValid() {
        Discipline discipline = new Discipline("name");
        Group group = new Group("name", discipline);
        assertDoesNotThrow(() -> validator.validate(group));
    }


    @ParameterizedTest
    @NullSource
    public void validate_whenGroupIsNull_throwIllegalArgumentException(Group nullGroup) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullGroup));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void validate_whenGroupNameFieldIsEmptyOrNull_throwValidationException(String name) {
        Discipline discipline = new Discipline("name");
        Group group = new Group(name, discipline);
        assertThrows(ValidationException.class, () -> validator.validate(group));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenGroupDisciplineIsNull_throwValidationException(Discipline discipline) {
        Group group = new Group("name", discipline);
        assertThrows(ValidationException.class, () -> validator.validate(group));
    }

    @Test
    public void validate_whenGroupNameIsNotUnique_throwValidationException() {
        String sharedName = "name";
        Discipline discipline = new Discipline("name");
        Group groupToValidate = new Group(sharedName, discipline);
        Group groupToBeFounded = new Group(sharedName, discipline);
        when(groupRepository.findByName(sharedName)).thenReturn(Optional.of(groupToBeFounded));
        assertThrows(ValidationException.class, () -> validator.validate(groupToValidate));
    }


}
