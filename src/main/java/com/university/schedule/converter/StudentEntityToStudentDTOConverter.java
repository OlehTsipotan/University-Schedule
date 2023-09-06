package com.university.schedule.converter;

import com.university.schedule.dto.StudentDTO;
import com.university.schedule.model.Student;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentEntityToStudentDTOConverter implements Converter<Student, StudentDTO> {

    private final ModelMapper modelMapper;

    public StudentEntityToStudentDTOConverter() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(Student.class, StudentDTO.class).addMappings(
                modelMapper -> modelMapper.map(Student::isEnable, StudentDTO::setIsEnable));
    }

    @Override
    public StudentDTO convert(Student source) {
        return modelMapper.map(source, StudentDTO.class);
    }
}
