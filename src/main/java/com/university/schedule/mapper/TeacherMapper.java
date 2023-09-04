package com.university.schedule.mapper;

import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class TeacherMapper {

    private final ModelMapper modelMapper;


    public TeacherMapper() {
        this.modelMapper = new ModelMapper();
        Converter<Set<Course>, List<String>> converter = c -> c.getSource().stream().map(Course::getName).toList();
        modelMapper.typeMap(Teacher.class, TeacherDTO.class).addMappings(
                modelMapper -> {modelMapper.using(converter)
                        .map(Teacher::getCourses, TeacherDTO::setCourseNames);
                modelMapper.map(Teacher::isEnable, TeacherDTO::setIsEnable);});

    }

    public TeacherDTO convertToDto(Teacher teacher) {
        return modelMapper.map(teacher, TeacherDTO.class);
    }
}
