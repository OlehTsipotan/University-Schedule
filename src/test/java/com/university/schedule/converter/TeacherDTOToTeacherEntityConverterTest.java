package com.university.schedule.converter;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.Course;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Role;
import com.university.schedule.model.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TeacherDTOToTeacherEntityConverterTest {

    private TeacherDTOToTeacherEntityConverter teacherDTOToTeacherEntityConverter;

    @BeforeEach
    public void setUp() {
        teacherDTOToTeacherEntityConverter = new TeacherDTOToTeacherEntityConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenTeacherIsNull_throwIllegalArgumentException(TeacherDTO teacherDTO) {
        assertThrows(IllegalArgumentException.class, () -> teacherDTOToTeacherEntityConverter.convert(teacherDTO));
    }

    @Test
    public void convert_success() {
        Role role = new Role(1L, "name");
        Course course = new Course(1L, "name");
        Teacher teacher = new Teacher("email", "password", "firstName", "lastName", role);
        teacher.setId(1L);
        teacher.setIsEnable(true);
        teacher.setCourses(Set.of(course));

        CourseDTO courseDTO = new CourseDTO(1L, "name");
        RoleDTO roleDTO = new RoleDTO(1L, "name");
        TeacherDTO teacherDTO = new TeacherDTO(1L, "email", "firstName", "lastName", roleDTO, true, List.of(courseDTO));

        Teacher teacherFound = teacherDTOToTeacherEntityConverter.convert(teacherDTO);

        assertEquals(teacher.getCourses(), teacherFound.getCourses());
        assertEquals(teacher.getRole(), teacherFound.getRole());
        assertEquals(teacher.isEnable(), teacherFound.isEnable());
        assertEquals(teacher.getFirstName(), teacherFound.getFirstName());
        assertEquals(teacher.getLastName(), teacherFound.getLastName());
        assertEquals(teacher.getEmail(), teacherFound.getEmail());
        assertEquals(teacher.getId(), teacherFound.getId());
    }

    @Test
    public void convert_whenStudentNullFields_success() {
        Teacher teacher = new Teacher();

        TeacherDTO teacherDTO = new TeacherDTO();

        Teacher teacherFound = teacherDTOToTeacherEntityConverter.convert(teacherDTO);

        assertEquals(teacher.getCourses(), teacherFound.getCourses());
        assertEquals(teacher.getRole(), teacherFound.getRole());
        assertEquals(teacher.isEnable(), teacherFound.isEnable());
        assertEquals(teacher.getFirstName(), teacherFound.getFirstName());
        assertEquals(teacher.getLastName(), teacherFound.getLastName());
        assertEquals(teacher.getEmail(), teacherFound.getEmail());
        assertEquals(teacher.getId(), teacherFound.getId());
    }
}
