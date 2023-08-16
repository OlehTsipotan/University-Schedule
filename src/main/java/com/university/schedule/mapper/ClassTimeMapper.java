package com.university.schedule.mapper;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.model.ClassTime;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ClassTimeMapper {

    private final ModelMapper modelMapper;


    public ClassTimeMapper() {
        this.modelMapper = new ModelMapper();
        Converter<Duration, Integer> converter = d -> Math.toIntExact(d.getSource().toMinutes());
        modelMapper.typeMap(ClassTime.class, ClassTimeDTO.class).addMappings(
                modelMapper -> modelMapper.using(converter).map(ClassTime::getDuration, ClassTimeDTO::setDurationMinutes));
    }

    public ClassTimeDTO convertToDto(ClassTime classTime) {
        return modelMapper.map(classTime, ClassTimeDTO.class);
    }
}