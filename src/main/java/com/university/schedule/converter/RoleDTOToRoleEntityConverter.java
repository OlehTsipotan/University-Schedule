package com.university.schedule.converter;

import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.model.Authority;
import com.university.schedule.model.Role;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleDTOToRoleEntityConverter implements Converter<RoleDTO, Role> {

	private final ModelMapper modelMapper;

	private final AuthorityDTOToAuthorityEntityConverter authorityDTOToAuthorityEntityConverter;


	public RoleDTOToRoleEntityConverter() {
		this.modelMapper = new ModelMapper();
		this.authorityDTOToAuthorityEntityConverter = new AuthorityDTOToAuthorityEntityConverter();

		org.modelmapper.Converter<List<AuthorityDTO>, Set<Authority>> authoritiesListConverter =
				courseList -> courseList.getSource().stream().map(authorityDTOToAuthorityEntityConverter::convert)
						.collect(Collectors.toSet());

		modelMapper.typeMap(RoleDTO.class, Role.class).addMappings(modelMapper -> {
			modelMapper.using(authoritiesListConverter).map(RoleDTO::getAuthorityDTOS, Role::setAuthorities);
		});
	}

	@Override
	public Role convert(RoleDTO source) {
		return modelMapper.map(source, Role.class);
	}
}
