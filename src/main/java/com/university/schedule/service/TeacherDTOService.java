package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherDTOService {

    private final TeacherService teacherService;

    private final ConverterService converterService;


    public List<TeacherDTO> findAll(Pageable pageable){
        return teacherService.findAll(pageable).stream().map(this::convert).toList();
    }

    private TeacherDTO convert(Teacher teacher){
        return converterService.convert(teacher, TeacherDTO.class);
    }
}