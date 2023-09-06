package com.university.schedule.converter;

import com.university.schedule.dto.TeacherUpdateDTO;
import com.university.schedule.model.Teacher;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TeacherEntityToTeacherUpdateDTOConverter implements Converter<Teacher, TeacherUpdateDTO> {

    private final ModelMapper modelMapper;

    public TeacherEntityToTeacherUpdateDTOConverter() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(Teacher.class, TeacherUpdateDTO.class).addMappings(
                modelMapper ->
                        modelMapper.map(Teacher::isEnable, TeacherUpdateDTO::setIsEnable));
    }

    @Override
    public TeacherUpdateDTO convert(Teacher source) {
        return modelMapper.map(source, TeacherUpdateDTO.class);
    }
}
