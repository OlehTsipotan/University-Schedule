package com.university.schedule.mapper;

import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.model.Classroom;
import com.university.schedule.model.ScheduledClass;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
public class ClassroomMapper {

    private final ModelMapper modelMapper;


    public ClassroomMapper() {
        this.modelMapper = new ModelMapper();
    }

    public ClassroomDTO convertToDto(Classroom classroom) {
        return modelMapper.map(classroom, ClassroomDTO.class);
    }
}
