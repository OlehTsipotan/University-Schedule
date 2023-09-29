package com.university.schedule.formatter;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.service.BuildingService;
import com.university.schedule.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StringToClassroomDTOFormatter implements Formatter<ClassroomDTO> {

	private final ClassroomService classroomService;

	@Override
	public ClassroomDTO parse(String text, Locale locale) throws ParseException {
		return classroomService.findByIdAsDTO(Long.parseLong(text));
	}

	@Override
	public String print(ClassroomDTO object, Locale locale) {
		return null;
	}
}
