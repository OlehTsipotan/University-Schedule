package com.university.schedule.converter;

import com.university.schedule.dto.TeacherUpdateDTO;
import com.university.schedule.model.Teacher;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class TeacherUpdateDTOToTeacherEntityConverter implements Converter<TeacherUpdateDTO, Teacher> {

    private final ModelMapper modelMapper;

    public TeacherUpdateDTOToTeacherEntityConverter() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(TeacherUpdateDTO.class, Teacher.class).addMappings(
                modelMapper ->
                        modelMapper.map(TeacherUpdateDTO::isEnable, Teacher::setIsEnable));
    }

    @Override
    public Teacher convert(TeacherUpdateDTO source) {
        return modelMapper.map(source, Teacher.class);
    }
}