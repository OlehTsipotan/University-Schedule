package com.university.schedule.converter;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Generic class, contains all Entity -> DTO and DTO -> entity converters implementations
 */
@Service
public class ConverterService extends GenericConversionService {

	@Autowired
	private List<Converter<?, ?>> _converters;

	@PostConstruct
	public void init() {
		_converters.forEach(this::addConverter);
	}
}
