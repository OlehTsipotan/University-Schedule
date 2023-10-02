package com.university.schedule.formatter;

import com.university.schedule.dto.AuthorityDTO;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class StringToAuthorityDTOFormatter implements Formatter<AuthorityDTO> {
	@Override
	public AuthorityDTO parse(String text, Locale locale) throws ParseException {
		return AuthorityDTO.builder().id(Long.parseLong(text)).build();
	}

	@Override
	public String print(AuthorityDTO object, Locale locale) {
		return null;
	}
}
