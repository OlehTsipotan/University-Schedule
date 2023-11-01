package com.university.schedule.converter;

import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.model.Classroom;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClassroomDTOToClassroomEntityConverter implements Converter<ClassroomDTO, Classroom> {

    private final ModelMapper modelMapper;


    public ClassroomDTOToClassroomEntityConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public Classroom convert(ClassroomDTO source) {
        return modelMapper.map(source, Classroom.class);
    }
}
