package com.university.schedule.converter;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.model.Building;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BuildingEntityToBuildingDTOConverter implements Converter<Building, BuildingDTO> {

    private final ModelMapper modelMapper;


    public BuildingEntityToBuildingDTOConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public BuildingDTO convert(Building source) {
        return modelMapper.map(source, BuildingDTO.class);
    }
}
