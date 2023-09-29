package com.university.schedule.formatter;

import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StringToAuthorityDTOFormatter implements Formatter<AuthorityDTO> {

	private final AuthorityService authorityService;

	@Override
	public AuthorityDTO parse(String text, Locale locale) throws ParseException {
		return authorityService.findByIdAsDTO(Long.parseLong(text));
	}

	@Override
	public String print(AuthorityDTO object, Locale locale) {
		return null;
	}
}
