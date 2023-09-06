package com.university.schedule.converter;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.model.ClassTime;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ClassTimeEntityToClassTimeDTOConverter implements Converter<ClassTime, ClassTimeDTO> {

    private final ModelMapper modelMapper;


    public ClassTimeEntityToClassTimeDTOConverter() {
        this.modelMapper = new ModelMapper();
        org.modelmapper.Converter<Duration, Integer> converter = d -> Math.toIntExact(d.getSource().toMinutes());
        modelMapper.typeMap(ClassTime.class, ClassTimeDTO.class).addMappings(
                modelMapper -> modelMapper.using(converter).map(ClassTime::getDuration, ClassTimeDTO::setDurationMinutes));
    }

    @Override
    public ClassTimeDTO convert(ClassTime source) {
        return modelMapper.map(source, ClassTimeDTO.class);
    }
}