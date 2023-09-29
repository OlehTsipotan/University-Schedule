package com.university.schedule.formatter;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.model.Role;
import com.university.schedule.service.DisciplineService;
import com.university.schedule.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StringToRoleDTOFormatter implements Formatter<RoleDTO> {

	private final RoleService roleService;

	@Override
	public RoleDTO parse(String text, Locale locale) throws ParseException {
		return roleService.findByIdAsDTO(Long.parseLong(text));
	}

	@Override
	public String print(RoleDTO object, Locale locale) {
		return null;
	}
}
