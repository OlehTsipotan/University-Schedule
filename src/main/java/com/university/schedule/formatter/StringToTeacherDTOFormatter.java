package com.university.schedule.formatter;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.Role;
import com.university.schedule.service.DisciplineService;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StringToTeacherDTOFormatter implements Formatter<TeacherDTO> {

	private final TeacherService teacherService;

	@Override
	public TeacherDTO parse(String text, Locale locale) throws ParseException {
		return teacherService.findByIdAsDTO(Long.parseLong(text));
	}

	@Override
	public String print(TeacherDTO object, Locale locale) {
		return null;
	}
}
