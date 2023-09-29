package com.university.schedule.formatter;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.service.BuildingService;
import com.university.schedule.service.ClassTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StringToClassTypeDTOFormatter implements Formatter<ClassTypeDTO> {

	private final ClassTypeService classTypeService;

	@Override
	public ClassTypeDTO parse(String text, Locale locale) throws ParseException {
		return classTypeService.findByIdAsDTO(Long.parseLong(text));
	}

	@Override
	public String print(ClassTypeDTO object, Locale locale) {
		return null;
	}
}
