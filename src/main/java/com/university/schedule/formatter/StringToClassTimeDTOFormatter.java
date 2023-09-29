package com.university.schedule.formatter;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.service.ClassTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StringToClassTimeDTOFormatter implements Formatter<ClassTimeDTO> {

	private final ClassTimeService classTimeService;

	@Override
	public ClassTimeDTO parse(String text, Locale locale) throws ParseException {
		return classTimeService.findByIdAsDTO(Long.parseLong(text));
	}

	@Override
	public String print(ClassTimeDTO object, Locale locale) {
		return null;
	}
}
