package com.university.schedule.converter;

import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.model.ClassType;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClassTypeEntityToClassTypeDTOConverter implements Converter<ClassType, ClassTypeDTO> {

	private final ModelMapper modelMapper;


	public ClassTypeEntityToClassTypeDTOConverter() {
		this.modelMapper = new ModelMapper();
	}

	@Override
	public ClassTypeDTO convert(ClassType source) {
		return modelMapper.map(source, ClassTypeDTO.class);
	}
}
