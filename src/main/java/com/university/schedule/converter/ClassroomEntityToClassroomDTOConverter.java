package com.university.schedule.converter;

import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.model.Classroom;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class ClassroomEntityToClassroomDTOConverter implements Converter<Classroom, ClassroomDTO> {

    private final ModelMapper modelMapper;


    public ClassroomEntityToClassroomDTOConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public ClassroomDTO convert(Classroom source) {
        return modelMapper.map(source, ClassroomDTO.class);
    }
}
