package com.university.schedule.converter;

import com.university.schedule.dto.StudentUpdateDTO;
import com.university.schedule.model.Student;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentEntityToStudentUpdateDTOConverter implements Converter<Student, StudentUpdateDTO> {

    private final ModelMapper modelMapper;

    public StudentEntityToStudentUpdateDTOConverter() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(Student.class, StudentUpdateDTO.class).addMappings(
                modelMapper ->
                        modelMapper.map(Student::isEnable, StudentUpdateDTO::setIsEnable));
    }

    @Override
    public StudentUpdateDTO convert(Student source) {
        return modelMapper.map(source, StudentUpdateDTO.class);
    }
}