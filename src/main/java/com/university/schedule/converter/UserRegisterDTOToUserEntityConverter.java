package com.university.schedule.converter;

import com.university.schedule.dto.UserRegisterDTO;
import com.university.schedule.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterDTOToUserEntityConverter implements Converter<UserRegisterDTO, User> {

    private final ModelMapper modelMapper;

    public UserRegisterDTOToUserEntityConverter() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(UserRegisterDTO.class, User.class);
    }

    @Override
    public User convert(UserRegisterDTO source) {
        User user = modelMapper.map(source, User.class);
        user.setIsEnable(true);
        return user;
    }
}