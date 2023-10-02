package com.university.schedule.converter;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.model.ClassTime;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ClassTimeDTOToClassTimeEntityConverter implements Converter<ClassTimeDTO, ClassTime> {

	private final ModelMapper modelMapper;


	public ClassTimeDTOToClassTimeEntityConverter() {
		this.modelMapper = new ModelMapper();
		org.modelmapper.Converter<Integer, Duration> converter =
				durationMinutes -> Duration.ofMinutes(durationMinutes.getSource());

		modelMapper.typeMap(ClassTimeDTO.class, ClassTime.class).addMappings(modelMapper -> modelMapper.using(converter)
				.map(ClassTimeDTO::getDurationMinutes, ClassTime::setDuration));
	}

	@Override
	public ClassTime convert(ClassTimeDTO source) {
		return modelMapper.map(source, ClassTime.class);
	}
}
