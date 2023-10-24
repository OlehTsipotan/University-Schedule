package com.university.schedule.converter;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.model.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourseEntityToCourseDTOConverterTest {

    private CourseEntityToCourseDTOConverter courseEntityToCourseDTOConverter;

    @BeforeEach
    public void setUp() {
        courseEntityToCourseDTOConverter = new CourseEntityToCourseDTOConverter();
    }

    @Test
    public void convert_whenCourseDTOIsValid_success() {
        CourseDTO courseDTO = new CourseDTO(1L, "name");
        Course course = new Course(1L, "name");
        assertEquals(courseDTO, courseEntityToCourseDTOConverter.convert(course));
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenCourseIsNull_throwIllegalArgumentException(Course nullCourse) {
        assertThrows(IllegalArgumentException.class, () -> courseEntityToCourseDTOConverter.convert(nullCourse));
    }
}
