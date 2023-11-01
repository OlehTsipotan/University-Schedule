package com.university.schedule.converter;

import com.university.schedule.dto.UserDTO;
import com.university.schedule.model.Teacher;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class UserDTOToTeacherEntityConverter implements Converter<UserDTO, Teacher> {

    private final ModelMapper modelMapper;

    public UserDTOToTeacherEntityConverter() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(UserDTO.class, Teacher.class)
            .addMappings(modelMapper -> modelMapper.map(UserDTO::isEnable, Teacher::setIsEnable));
    }

    @Override
    public Teacher convert(UserDTO source) {
        return modelMapper.map(source, Teacher.class);
    }
}