package com.university.schedule.converter;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.model.ClassTime;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ClassTimeEntityToClassTimeDTOConverter implements Converter<ClassTime, ClassTimeDTO> {

    private final ModelMapper modelMapper;


    public ClassTimeEntityToClassTimeDTOConverter() {
        this.modelMapper = new ModelMapper();

        // Define a custom converter for Duration to Integer
        org.modelmapper.Converter<Duration, Integer> durationConverter =
            ctx -> Math.toIntExact(ctx.getSource().toMinutes());

        org.modelmapper.Condition<Duration, Integer> nonNull = ctx -> ctx.getSource() != null;

        // Use the custom converter for duration mapping
        modelMapper.typeMap(ClassTime.class, ClassTimeDTO.class).addMappings(
            mapper -> mapper.when(nonNull).using(durationConverter)
                .map(ClassTime::getDuration, ClassTimeDTO::setDurationMinutes));
    }

    @Override
    public ClassTimeDTO convert(ClassTime source) {
        return modelMapper.map(source, ClassTimeDTO.class);
    }
}