package com.university.schedule.converter;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.StudentDTO;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import com.university.schedule.model.Role;
import com.university.schedule.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StudentEntityToStudentDTOConverterTest {

    private StudentEntityToStudentDTOConverter studentEntityToStudentDTOConverter;

    @BeforeEach
    void setUp() {
        studentEntityToStudentDTOConverter = new StudentEntityToStudentDTOConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenStudentIsNull_throwIllegalArgumentException(Student student) {
        assertThrows(IllegalArgumentException.class, () -> studentEntityToStudentDTOConverter.convert(student));
    }

    @Test
    public void convert_success() {
        Role role = new Role(1L, "name");
        Discipline discipline = new Discipline(1L, "name");
        Group group = new Group(1L, "name", discipline);
        Student student = new Student("email", "password", "firstName", "lastName", role);
        student.setId(1L);
        student.setIsEnable(true);
        student.setGroup(group);

        DisciplineDTO disciplineDTO = new DisciplineDTO(1L, "name");
        GroupDTO groupDTO = GroupDTO.builder().id(1L).name("name").disciplineDTO(disciplineDTO).build();
        RoleDTO roleDTO = new RoleDTO(1L, "name");
        StudentDTO studentDTO = new StudentDTO(1L, "email", "firstName", "lastName", roleDTO, true, groupDTO);

        StudentDTO studentDTOFound = studentEntityToStudentDTOConverter.convert(student);

        assertEquals(studentDTO.getGroupDTO(), studentDTOFound.getGroupDTO());
        assertEquals(studentDTO.getRoleDTO(), studentDTOFound.getRoleDTO());
        assertEquals(studentDTO.isEnable(), studentDTOFound.isEnable());
        assertEquals(studentDTO.getFirstName(), studentDTOFound.getFirstName());
        assertEquals(studentDTO.getLastName(), studentDTOFound.getLastName());
        assertEquals(studentDTO.getEmail(), studentDTOFound.getEmail());
        assertEquals(studentDTO.getId(), studentDTOFound.getId());
    }

    @Test
    public void convert_whenStudentNullFields_success(){
        Student student = new Student();

        StudentDTO studentDTO = new StudentDTO();

        StudentDTO studentDTOFound = studentEntityToStudentDTOConverter.convert(student);

        assertEquals(studentDTO.getGroupDTO(), studentDTOFound.getGroupDTO());
        assertEquals(studentDTO.getRoleDTO(), studentDTOFound.getRoleDTO());
        assertEquals(studentDTO.isEnable(), studentDTOFound.isEnable());
        assertEquals(studentDTO.getFirstName(), studentDTOFound.getFirstName());
        assertEquals(studentDTO.getLastName(), studentDTOFound.getLastName());
        assertEquals(studentDTO.getEmail(), studentDTOFound.getEmail());
        assertEquals(studentDTO.getId(), studentDTOFound.getId());
    }
}
