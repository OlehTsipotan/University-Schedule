package com.university.schedule.converter;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.model.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourseDTOToCourseEntityConverterTest {

    private CourseDTOToCourseEntityConverter courseDTOToCourseEntityConverter;

    @BeforeEach
    public void setUp() {
        courseDTOToCourseEntityConverter = new CourseDTOToCourseEntityConverter();
    }

    @Test
    public void convert_whenCourseDTOIsValid_success() {
        CourseDTO courseDTO = new CourseDTO(1L, "name");
        Course course = new Course(1L, "name");
        assertEquals(course, courseDTOToCourseEntityConverter.convert(courseDTO));
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenCourseDTOIsNull_throwIllegalArgumentException(CourseDTO nullCourseDTO) {
        assertThrows(IllegalArgumentException.class, () -> courseDTOToCourseEntityConverter.convert(nullCourseDTO));
    }
}

