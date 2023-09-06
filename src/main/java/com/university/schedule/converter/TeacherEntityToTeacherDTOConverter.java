package com.university.schedule.converter;

import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class TeacherEntityToTeacherDTOConverter implements Converter<Teacher, TeacherDTO> {

    private final ModelMapper modelMapper;


    public TeacherEntityToTeacherDTOConverter() {
        this.modelMapper = new ModelMapper();
        org.modelmapper.Converter<Set<Course>, List<String>> converter = c -> c.getSource().stream().map(Course::getName).toList();
        modelMapper.typeMap(Teacher.class, TeacherDTO.class).addMappings(
                modelMapper -> {modelMapper.using(converter)
                        .map(Teacher::getCourses, TeacherDTO::setCourseNames);
                modelMapper.map(Teacher::isEnable, TeacherDTO::setIsEnable);});

    }

    @Override
    public TeacherDTO convert(Teacher source) {
        return modelMapper.map(source, TeacherDTO.class);
    }
}
