package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.StudentUpdateDTO;
import com.university.schedule.dto.UserUpdateDTO;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentUpdateDTOService {

    private final StudentService studentService;

    private final ConverterService converterService;

    public Long save(StudentUpdateDTO studentUpdateDTO) {
        Student studentToSave = this.convertToStudentEntity(studentUpdateDTO);
        studentToSave.setPassword(studentService.findById(studentUpdateDTO.getId()).getPassword());
        return studentService.save(studentToSave);
    }

    public StudentUpdateDTO findById(Long id){
        Student student = studentService.findById(id);
        return this.convertToStudentUpdateDTO(student);
    }

    private Student convertToStudentEntity(StudentUpdateDTO studentUpdateDTO){
        return converterService.convert(studentUpdateDTO, Student.class);
    }

    private StudentUpdateDTO convertToStudentUpdateDTO(Student student){
        return converterService.convert(student, StudentUpdateDTO.class);
    }
}
