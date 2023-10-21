package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.*;
import com.university.schedule.repository.ScheduledClassRepository;
import com.university.schedule.service.CourseService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ScheduledClassEntityValidatorTest {

    private ScheduledClassEntityValidator validator;

    @Mock
    private ScheduledClassRepository scheduledClassRepository;

    @Mock
    private CourseService courseService;

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setUp() {
        validator = new ScheduledClassEntityValidator(scheduledClassRepository, courseService, jakartaValidator);
    }

    public ScheduledClass createScheduledClass() {
        ScheduledClass scheduledClass = new ScheduledClass();

        Teacher teacher = new Teacher("email", "password", "name", "surname", new Role("ROLE_TEACHER"));
        Course course = new Course("course");
        teacher.setCourses(Set.of(course));
        Group group = new Group("group", new Discipline("discipline"));
        group.setCourses(Set.of(course));

        scheduledClass.setCourse(course);
        scheduledClass.setClassroom(new Classroom("classroom", new Building("building", "address")));
        scheduledClass.setTeacher(teacher);
        scheduledClass.setGroups(Set.of(group));
        scheduledClass.setClassTime(new ClassTime(1, LocalTime.of(8, 0), Duration.ofMinutes(90)));
        scheduledClass.setClassType(new ClassType("classType"));
        scheduledClass.setDate(LocalDate.of(2021, 1, 1));
        return scheduledClass;
    }

    @Test
    public void validate_whenScheduledClassIsValid() {
        ScheduledClass scheduledClass = createScheduledClass();

        when(courseService.findByTeacher(scheduledClass.getTeacher())).thenReturn(List.of(scheduledClass.getCourse()));
        when(courseService.findByGroup(any())).thenReturn(List.of(scheduledClass.getCourse()));
        when(scheduledClassRepository.findByDateAndClassTimeAndTeacher(scheduledClass.getDate(),
            scheduledClass.getClassTime(), scheduledClass.getTeacher())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validate(scheduledClass));

        verify(courseService).findByTeacher(scheduledClass.getTeacher());
        verify(courseService).findByGroup(any());
        verify(scheduledClassRepository).findByDateAndClassTimeAndTeacher(scheduledClass.getDate(),
            scheduledClass.getClassTime(), scheduledClass.getTeacher());
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenScheduledClassIsNull_throwIllegalArgumentException(ScheduledClass nullScheduledClass) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullScheduledClass));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenScheduledClassCourseIsNull_throwValidationException(Course course) {
        ScheduledClass scheduledClass = createScheduledClass();
        scheduledClass.setCourse(course);
        assertThrows(ValidationException.class, () -> validator.validate(scheduledClass));
    }

    @Test
    public void validate_whenScheduledClassTeacherDateClassTimePairIsNotUnique_throwValidationException() {
        ScheduledClass scheduledClassToCheck = createScheduledClass();
        ScheduledClass scheduledClassToBeFounded = createScheduledClass();
        when(scheduledClassRepository.findByDateAndClassTimeAndTeacher(scheduledClassToCheck.getDate(),
            scheduledClassToCheck.getClassTime(), scheduledClassToCheck.getTeacher())).thenReturn(
            Optional.of(scheduledClassToBeFounded));
        when(courseService.findByTeacher(scheduledClassToCheck.getTeacher())).thenReturn(List.of(scheduledClassToCheck.getCourse()));
        when(courseService.findByGroup(any())).thenReturn(List.of(scheduledClassToCheck.getCourse()));
        assertThrows(ValidationException.class, () -> validator.validate(scheduledClassToCheck));
    }

    @Test
    public void validate_whenScheduledClassTeacherIsNotAssignedToCourse_throwValidationException() {
        ScheduledClass scheduledClass = createScheduledClass();
        when(courseService.findByTeacher(scheduledClass.getTeacher())).thenReturn(new ArrayList<>());
        when(courseService.findByGroup(any())).thenReturn(List.of(scheduledClass.getCourse()));
        assertThrows(ValidationException.class, () -> validator.validate(scheduledClass));
    }

    @Test
    public void validate_whenScheduledClassGroupIsNotAssignedToCourse_throwValidationException() {
        ScheduledClass scheduledClass = createScheduledClass();
        when(courseService.findByGroup(any())).thenReturn(new ArrayList<>());
        when(courseService.findByTeacher(scheduledClass.getTeacher())).thenReturn(List.of(scheduledClass.getCourse()));
        assertThrows(ValidationException.class, () -> validator.validate(scheduledClass));
    }

    @Test
    public void validate_whenScheduledClassFieldsAreNull_throwValidationException(){
        ScheduledClass scheduledClass = new ScheduledClass();
        assertThrows(ValidationException.class, () -> validator.validate(scheduledClass));
    }


}
