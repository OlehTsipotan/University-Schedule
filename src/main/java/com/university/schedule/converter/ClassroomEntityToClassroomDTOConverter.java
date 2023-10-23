package com.university.schedule.converter;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class ClassroomEntityToClassroomDTOConverter implements Converter<Classroom, ClassroomDTO> {

    private final ModelMapper modelMapper;

    private final BuildingEntityToBuildingDTOConverter buildingEntityToBuildingDTOConverter;

    public ClassroomEntityToClassroomDTOConverter() {
        this.modelMapper = new ModelMapper();
        this.buildingEntityToBuildingDTOConverter = new BuildingEntityToBuildingDTOConverter();
        org.modelmapper.Converter<Building, BuildingDTO> converter =
            building -> buildingEntityToBuildingDTOConverter.convert(building.getSource());

        Condition notNull = ctx -> ctx.getSource() != null;

        modelMapper.typeMap(Classroom.class, ClassroomDTO.class).addMappings(
            modelMapper -> modelMapper.when(notNull).using(converter)
                .map(Classroom::getBuilding, ClassroomDTO::setBuildingDTO));
    }

    @Override
    public ClassroomDTO convert(Classroom source) {
        return modelMapper.map(source, ClassroomDTO.class);
    }
}
