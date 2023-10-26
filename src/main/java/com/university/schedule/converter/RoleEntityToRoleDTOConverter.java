package com.university.schedule.converter;

import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.model.Authority;
import com.university.schedule.model.Role;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class RoleEntityToRoleDTOConverter implements Converter<Role, RoleDTO> {

	private final ModelMapper modelMapper;

	private final AuthorityEntityToAuthorityDTOConverter authorityEntityToAuthorityDTOConverter;


	public RoleEntityToRoleDTOConverter() {
		this.modelMapper = new ModelMapper();
		this.authorityEntityToAuthorityDTOConverter = new AuthorityEntityToAuthorityDTOConverter();

		org.modelmapper.Converter<Set<Authority>, List<AuthorityDTO>> authoritiesListConverter =
				courseList -> courseList.getSource().stream().map(authorityEntityToAuthorityDTOConverter::convert)
						.toList();

        Condition notNull = ctx -> ctx.getSource() != null;

		modelMapper.typeMap(Role.class, RoleDTO.class).addMappings(modelMapper -> {
			modelMapper.when(notNull).using(authoritiesListConverter).map(Role::getAuthorities, RoleDTO::setAuthorityDTOS);
		});
	}

	@Override
	public RoleDTO convert(Role source) {
		return modelMapper.map(source, RoleDTO.class);
	}
}
