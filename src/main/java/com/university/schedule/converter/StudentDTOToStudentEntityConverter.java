package com.university.schedule.converter;

import com.university.schedule.dto.StudentDTO;
import com.university.schedule.model.Student;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentDTOToStudentEntityConverter implements Converter<StudentDTO, Student> {

    private final ModelMapper modelMapper;

    public StudentDTOToStudentEntityConverter() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(StudentDTO.class, Student.class)
            .addMappings(modelMapper -> modelMapper.map(StudentDTO::isEnable, Student::setIsEnable));
    }

    @Override
    public Student convert(StudentDTO source) {
        return modelMapper.map(source, Student.class);
    }
}
