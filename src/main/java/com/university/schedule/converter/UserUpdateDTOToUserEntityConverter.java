package com.university.schedule.converter;

import com.university.schedule.dto.UserUpdateDTO;
import com.university.schedule.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserUpdateDTOToUserEntityConverter implements Converter<UserUpdateDTO, User> {

    private final ModelMapper modelMapper;

    public UserUpdateDTOToUserEntityConverter() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(UserUpdateDTO.class, User.class).addMappings(modelMapper ->
                modelMapper.map(UserUpdateDTO::isEnable, User::setIsEnable));
    }

    @Override
    public User convert(UserUpdateDTO source) {
        return modelMapper.map(source, User.class);
    }
}
