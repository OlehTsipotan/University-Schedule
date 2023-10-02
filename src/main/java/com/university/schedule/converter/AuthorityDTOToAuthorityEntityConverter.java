package com.university.schedule.converter;

import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.model.Authority;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class AuthorityDTOToAuthorityEntityConverter implements Converter<AuthorityDTO, Authority> {

	private final ModelMapper modelMapper;


	public AuthorityDTOToAuthorityEntityConverter() {
		this.modelMapper = new ModelMapper();
	}

	@Override
	public Authority convert(AuthorityDTO source) {
		return modelMapper.map(source, Authority.class);
	}
}