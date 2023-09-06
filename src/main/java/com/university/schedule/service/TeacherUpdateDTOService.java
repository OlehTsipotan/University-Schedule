package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.TeacherUpdateDTO;
import com.university.schedule.model.Teacher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherUpdateDTOService {

    private final TeacherService teacherService;

    private final ConverterService converterService;

    public Long save(TeacherUpdateDTO teacherUpdateDTO) {
        Teacher teacherToSave = this.convertToTeacherEntity(teacherUpdateDTO);
        teacherToSave.setPassword(teacherService.findById(teacherUpdateDTO.getId()).getPassword());
        return teacherService.save(teacherToSave);
    }

    public TeacherUpdateDTO findById(Long id){
        Teacher teacher = teacherService.findById(id);
        return this.convertToTeacherUpdateDTO(teacher);
    }

    private Teacher convertToTeacherEntity(TeacherUpdateDTO teacherUpdateDTO){
        return converterService.convert(teacherUpdateDTO, Teacher.class);
    }

    private TeacherUpdateDTO convertToTeacherUpdateDTO(Teacher teacher){
        return converterService.convert(teacher, TeacherUpdateDTO.class);
    }
}
