package com.university.schedule.mapper;

import com.university.schedule.dto.StudentDTO;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    private final ModelMapper modelMapper;

    public StudentMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(Student.class, StudentDTO.class).addMappings(
                modelMapper -> modelMapper.map(Student::isEnable, StudentDTO::setIsEnable));
    }

    public StudentDTO convertToDto(Student student) {
        return modelMapper.map(student, StudentDTO.class);
    }
}
