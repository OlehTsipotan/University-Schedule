package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.model.Classroom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomDTOService {

    private final ClassroomService classroomService;

    private final ConverterService converterService;


    public List<ClassroomDTO> findAll(Pageable pageable){
        return classroomService.findAll(pageable).stream().map(this::convert).toList();
    }

    public List<ClassroomDTO> findAll(){
        return classroomService.findAll().stream().map(this::convert).toList();
    }

    private ClassroomDTO convert(Classroom classroom){
        return converterService.convert(classroom, ClassroomDTO.class);
    }
}
