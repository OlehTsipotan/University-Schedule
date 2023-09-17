package com.university.schedule.converter;

import com.university.schedule.dto.RoleDTO;
import com.university.schedule.model.Role;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoleEntityToRoleDTOConverter implements Converter<Role, RoleDTO> {

	private final ModelMapper modelMapper;


	public RoleEntityToRoleDTOConverter() {
		this.modelMapper = new ModelMapper();
	}

	@Override
	public RoleDTO convert(Role source) {
		return modelMapper.map(source, RoleDTO.class);
	}
}
