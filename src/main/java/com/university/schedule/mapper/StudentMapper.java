package com.university.schedule.mapper;

import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.dto.StudentDTO;
import com.university.schedule.model.Group;
import com.university.schedule.model.ScheduledClass;
import com.university.schedule.model.Student;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class StudentMapper {

    private final ModelMapper modelMapper;


    public StudentMapper() {
        this.modelMapper = new ModelMapper();
    }

    public StudentDTO convertToDto(Student student) {
        return modelMapper.map(student, StudentDTO.class);
    }
}
