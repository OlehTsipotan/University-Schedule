package com.university.schedule.converter;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.model.Discipline;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DisciplineDTOToDisciplineEntityConverter implements Converter<DisciplineDTO, Discipline> {

	private final ModelMapper modelMapper;


	public DisciplineDTOToDisciplineEntityConverter() {
		this.modelMapper = new ModelMapper();
	}

	@Override
	public Discipline convert(DisciplineDTO source) {
		return modelMapper.map(source, Discipline.class);
	}
}
