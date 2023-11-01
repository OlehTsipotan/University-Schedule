package com.university.schedule.formatter;

import com.university.schedule.dto.AuthorityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
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
