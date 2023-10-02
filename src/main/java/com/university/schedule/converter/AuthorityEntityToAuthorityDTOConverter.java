package com.university.schedule.converter;

import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.model.Authority;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AuthorityEntityToAuthorityDTOConverter implements Converter<Authority, AuthorityDTO> {

	private final ModelMapper modelMapper;


	public AuthorityEntityToAuthorityDTOConverter() {
		this.modelMapper = new ModelMapper();
	}

	@Override
	public AuthorityDTO convert(Authority source) {
		return modelMapper.map(source, AuthorityDTO.class);
	}
}


