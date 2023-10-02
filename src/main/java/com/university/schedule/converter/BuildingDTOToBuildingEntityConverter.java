package com.university.schedule.converter;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.model.Building;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BuildingDTOToBuildingEntityConverter implements Converter<BuildingDTO, Building> {

	private final ModelMapper modelMapper;


	public BuildingDTOToBuildingEntityConverter() {
		this.modelMapper = new ModelMapper();
	}

	@Override
	public Building convert(BuildingDTO source) {
		return modelMapper.map(source, Building.class);
	}
}
