package com.university.schedule.converter;

import com.university.schedule.dto.UserUpdateDTO;
import com.university.schedule.model.Teacher;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class UserUpdateDTOToTeacherEntityConverter implements Converter<UserUpdateDTO, Teacher> {

    private final ModelMapper modelMapper;

    public UserUpdateDTOToTeacherEntityConverter() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(UserUpdateDTO.class, Teacher.class).addMappings(modelMapper ->
                modelMapper.map(UserUpdateDTO::isEnable, Teacher::setIsEnable));
    }

    @Override
    public Teacher convert(UserUpdateDTO source) {
        return modelMapper.map(source, Teacher.class);
    }
}