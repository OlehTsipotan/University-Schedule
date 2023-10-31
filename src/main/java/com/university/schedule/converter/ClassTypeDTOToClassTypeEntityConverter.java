package com.university.schedule.converter;

import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.model.ClassType;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClassTypeDTOToClassTypeEntityConverter implements Converter<ClassTypeDTO, ClassType> {

    private final ModelMapper modelMapper;


    public ClassTypeDTOToClassTypeEntityConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ClassType convert(ClassTypeDTO source) {
        return modelMapper.map(source, ClassType.class);
    }
}
