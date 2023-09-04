package com.university.schedule.converter;

import org.springframework.format.Formatter;
import java.text.ParseException;
import java.time.Duration;
import java.util.Locale;

/**
 * For Thymeleaf updateForm
 */
public class DurationFormConverter implements Formatter<Duration> {
    @Override
    public Duration parse(String text, Locale locale) throws ParseException {
        return Duration.ofMinutes(Integer.parseInt(text));
    }

    @Override
    public String print(Duration duration, Locale locale) {
        return duration.toString();
    }
}