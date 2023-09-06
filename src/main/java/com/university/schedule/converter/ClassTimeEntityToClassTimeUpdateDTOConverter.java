package com.university.schedule.converter;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.dto.ClassTimeUpdateDTO;
import com.university.schedule.model.ClassTime;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ClassTimeEntityToClassTimeUpdateDTOConverter implements Converter<ClassTime, ClassTimeUpdateDTO> {

    private final ModelMapper modelMapper;


    public ClassTimeEntityToClassTimeUpdateDTOConverter() {
        this.modelMapper = new ModelMapper();
        org.modelmapper.Converter<Duration, Integer> converter = duration -> Math.toIntExact(duration.getSource().toMinutes());
        modelMapper.typeMap(ClassTime.class, ClassTimeUpdateDTO.class).addMappings(modelMapper ->
                modelMapper.using(converter).map(ClassTime::getDuration, ClassTimeUpdateDTO::setDurationMinutes));
    }

    @Override
    public ClassTimeUpdateDTO convert(ClassTime source) {
        return modelMapper.map(source, ClassTimeUpdateDTO.class);
    }
}
