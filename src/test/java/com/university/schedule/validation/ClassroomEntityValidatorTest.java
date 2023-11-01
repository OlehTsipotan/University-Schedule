package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import com.university.schedule.repository.ClassroomRepository;
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
public class ClassroomEntityValidatorTest {

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();
    private ClassroomEntityValidator validator;
    @Mock
    private ClassroomRepository classroomRepository;

    @BeforeEach
    public void setUp() {
        validator = new ClassroomEntityValidator(classroomRepository, jakartaValidator);
    }

    @Test
    public void validate_whenClassroomIsValid() {
        Building building = Building.builder().id(1L).name("name").address("address").build();
        Classroom classroom = Classroom.builder().id(1L).name("name").building(building).build();
        assertDoesNotThrow(() -> validator.validate(classroom));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenClassroomIsNull_throwIllegalArgumentException(Classroom nullClassroom) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullClassroom));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void validate_whenClassroomNameFieldIsEmptyOrNull_throwValidationException(String name) {
        Building building = Building.builder().id(1L).name("name").address("address").build();
        Classroom classroom = Classroom.builder().id(1L).name(name).building(building).build();
        assertThrows(ValidationException.class, () -> validator.validate(classroom));
    }

    @Test
    public void validate_whenClassroomNameAndBuildingPairIsNotUnique_throwValidationException() {
        String sharedName = "name";
        Building building = Building.builder().id(1L).name("name").address("address").build();
        Classroom classroomToValidate = Classroom.builder().id(1L).name(sharedName).building(building).build();
        Classroom classroomToBeFounded = Classroom.builder().id(2L).name(sharedName).building(building).build();
        when(classroomRepository.findByNameAndBuilding(sharedName, building)).thenReturn(
            Optional.of(classroomToBeFounded));
        assertThrows(ValidationException.class, () -> validator.validate(classroomToValidate));
    }


}
