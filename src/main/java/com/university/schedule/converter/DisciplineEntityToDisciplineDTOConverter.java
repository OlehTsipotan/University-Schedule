package com.university.schedule.converter;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.model.Discipline;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DisciplineEntityToDisciplineDTOConverter implements Converter<Discipline, DisciplineDTO> {

    private final ModelMapper modelMapper;


    public DisciplineEntityToDisciplineDTOConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public DisciplineDTO convert(Discipline source) {
        return modelMapper.map(source, DisciplineDTO.class);
    }
}