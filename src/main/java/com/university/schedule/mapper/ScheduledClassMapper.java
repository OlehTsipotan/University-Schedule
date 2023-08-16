package com.university.schedule.mapper;

import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.model.Group;
import com.university.schedule.model.ScheduledClass;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public class ScheduledClassMapper {

    private final ModelMapper modelMapper;


    public ScheduledClassMapper() {
        this.modelMapper = new ModelMapper();

        Converter<Set<Group>, List<String>> converter = c -> c.getSource().stream().map(Group::getName).toList();
        modelMapper.typeMap(ScheduledClass.class, ScheduledClassDTO.class).addMappings(
                modelMapper -> modelMapper.using(converter)
                        .map(ScheduledClass::getGroups, ScheduledClassDTO::setGroupNames));
    }

    public ScheduledClassDTO convertToDto(ScheduledClass scheduledClass) {
        return modelMapper.map(scheduledClass, ScheduledClassDTO.class);
    }
}

