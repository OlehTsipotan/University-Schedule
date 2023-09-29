package com.university.schedule.formatter;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StringToCourseDTOFormatter implements Formatter<CourseDTO> {

	private final CourseService courseService;

	@Override
	public CourseDTO parse(String text, Locale locale) throws ParseException {
		return courseService.findByIdAsDTO(Long.parseLong(text));
	}

	@Override
	public String print(CourseDTO object, Locale locale) {
		return null;
	}
}
