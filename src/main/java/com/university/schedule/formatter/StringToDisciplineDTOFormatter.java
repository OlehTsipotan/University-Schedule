package com.university.schedule.formatter;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.service.DisciplineService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StringToDisciplineDTOFormatter implements Formatter<DisciplineDTO> {

	private final DisciplineService disciplineService;

	@Override
	public DisciplineDTO parse(String text, Locale locale) throws ParseException {
		return disciplineService.findByIdAsDTO(Long.parseLong(text));
	}

	@Override
	public String print(DisciplineDTO object, Locale locale) {
		return null;
	}
}
