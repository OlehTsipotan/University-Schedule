package com.university.schedule.converter;

import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.model.Group;
import com.university.schedule.model.ScheduledClass;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public class ScheduledClassEntityToScheduledClassDTOConverter implements Converter<ScheduledClass, ScheduledClassDTO> {

    private final ModelMapper modelMapper;


    public ScheduledClassEntityToScheduledClassDTOConverter() {
        this.modelMapper = new ModelMapper();

        org.modelmapper.Converter<Set<Group>, List<String>> converter = c -> c.getSource().stream().map(Group::getName).toList();
        modelMapper.typeMap(ScheduledClass.class, ScheduledClassDTO.class).addMappings(
                modelMapper -> modelMapper.using(converter)
                        .map(ScheduledClass::getGroups, ScheduledClassDTO::setGroupNames));
    }

    @Override
    public ScheduledClassDTO convert(ScheduledClass source) {
        return modelMapper.map(source, ScheduledClassDTO.class);
    }
}

