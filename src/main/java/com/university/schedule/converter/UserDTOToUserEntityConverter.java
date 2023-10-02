package com.university.schedule.converter;

import com.university.schedule.dto.UserDTO;
import com.university.schedule.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDTOToUserEntityConverter implements Converter<UserDTO, User> {

	private final ModelMapper modelMapper;

	public UserDTOToUserEntityConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.typeMap(UserDTO.class, User.class)
				.addMappings(modelMapper -> modelMapper.map(UserDTO::isEnable, User::setIsEnable));
	}

	@Override
	public User convert(UserDTO source) {
		return modelMapper.map(source, User.class);
	}
}
