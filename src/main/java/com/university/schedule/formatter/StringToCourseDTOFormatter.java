package com.university.schedule.formatter;

import com.university.schedule.dto.CourseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StringToCourseDTOFormatter implements Formatter<CourseDTO> {

	@Override
	public CourseDTO parse(String text, Locale locale) throws ParseException {
		return CourseDTO.builder().id(Long.parseLong(text)).build();
	}

	@Override
	public String print(CourseDTO object, Locale locale) {
		return null;
	}
}
