package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Course;
import com.university.schedule.repository.CourseRepository;
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
public class CourseEntityValidatorTest {

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();
    private CourseEntityValidator validator;
    @Mock
    private CourseRepository courseRepository;

    @BeforeEach
    public void setUp() {
        validator = new CourseEntityValidator(courseRepository, jakartaValidator);
    }

    @Test
    public void validate_whenCourseIsValid() {
        Course course = new Course("name");
        assertDoesNotThrow(() -> validator.validate(course));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenCourseIsNull_throwIllegalArgumentException(Course nullCourse) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullCourse));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void validate_whenCourseNameFieldIsEmptyOrNull_throwValidationException(String name) {
        Course course = new Course(name);
        assertThrows(ValidationException.class, () -> validator.validate(course));
    }

    @Test
    public void validate_whenCourseNameIsNotUnique_throwValidationException() {
        String sharedName = "name";

        Course courseToCheck = new Course(sharedName);
        Course courseToBeFounded = new Course(sharedName);
        when(courseRepository.findByName(sharedName)).thenReturn(Optional.of(courseToBeFounded));
        assertThrows(ValidationException.class, () -> validator.validate(courseToCheck));
    }

}
