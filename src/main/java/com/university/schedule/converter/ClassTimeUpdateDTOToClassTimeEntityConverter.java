package com.university.schedule.converter;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.dto.ClassTimeUpdateDTO;
import com.university.schedule.model.ClassTime;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ClassTimeUpdateDTOToClassTimeEntityConverter implements Converter<ClassTimeUpdateDTO, ClassTime> {

    private final ModelMapper modelMapper;


    public ClassTimeUpdateDTOToClassTimeEntityConverter() {
        this.modelMapper = new ModelMapper();
        org.modelmapper.Converter<Integer, Duration> converter = durationMinutes -> Duration.ofMinutes(durationMinutes.getSource());
        modelMapper.typeMap(ClassTimeUpdateDTO.class, ClassTime.class).addMappings(
                modelMapper -> modelMapper.using(converter).map(ClassTimeUpdateDTO::getDurationMinutes, ClassTime::setDuration));
    }

    @Override
    public ClassTime convert(ClassTimeUpdateDTO source) {
        return modelMapper.map(source, ClassTime.class);
    }
}
