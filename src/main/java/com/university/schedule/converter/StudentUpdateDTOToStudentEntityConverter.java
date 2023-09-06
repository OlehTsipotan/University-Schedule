package com.university.schedule.converter;

import com.university.schedule.dto.StudentUpdateDTO;
import com.university.schedule.model.Student;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentUpdateDTOToStudentEntityConverter implements Converter<StudentUpdateDTO, Student> {

    private final ModelMapper modelMapper;

    public StudentUpdateDTOToStudentEntityConverter() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(StudentUpdateDTO.class, Student.class).addMappings(
                modelMapper ->
                        modelMapper.map(StudentUpdateDTO::isEnable, Student::setIsEnable));
    }

    @Override
    public Student convert(StudentUpdateDTO source) {
        return modelMapper.map(source, Student.class);
    }
}
