package com.university.schedule.converter;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.Course;
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

public class TeacherEntityToTeacherDTOConverterTest {

    private TeacherEntityToTeacherDTOConverter teacherEntityToTeacherDTOConverter;

    @BeforeEach
    public void setUp() {
        teacherEntityToTeacherDTOConverter = new TeacherEntityToTeacherDTOConverter();
    }


    @ParameterizedTest
    @NullSource
    public void convert_whenTeacherIsNull_throwIllegalArgumentException(Teacher teacher) {
        assertThrows(IllegalArgumentException.class, () -> teacherEntityToTeacherDTOConverter.convert(teacher));
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

        TeacherDTO teacherDTOFound = teacherEntityToTeacherDTOConverter.convert(teacher);

        assertEquals(teacherDTO.getCourseDTOS(), teacherDTOFound.getCourseDTOS());
        assertEquals(teacherDTO.getRoleDTO(), teacherDTOFound.getRoleDTO());
        assertEquals(teacherDTO.isEnable(), teacherDTOFound.isEnable());
        assertEquals(teacherDTO.getFirstName(), teacherDTOFound.getFirstName());
        assertEquals(teacherDTO.getLastName(), teacherDTOFound.getLastName());
        assertEquals(teacherDTO.getEmail(), teacherDTOFound.getEmail());
        assertEquals(teacherDTO.getId(), teacherDTOFound.getId());
    }

    @Test
    public void convert_whenStudentNullFields_success() {
        Teacher teacher = new Teacher();

        TeacherDTO teacherDTO = new TeacherDTO();

        TeacherDTO teacherDTOFound = teacherEntityToTeacherDTOConverter.convert(teacher);

        assertEquals(teacherDTO.getCourseDTOS(), teacherDTOFound.getCourseDTOS());
        assertEquals(teacherDTO.getRoleDTO(), teacherDTOFound.getRoleDTO());
        assertEquals(teacherDTO.isEnable(), teacherDTOFound.isEnable());
        assertEquals(teacherDTO.getFirstName(), teacherDTOFound.getFirstName());
        assertEquals(teacherDTO.getLastName(), teacherDTOFound.getLastName());
        assertEquals(teacherDTO.getEmail(), teacherDTOFound.getEmail());
        assertEquals(teacherDTO.getId(), teacherDTOFound.getId());
    }
}
