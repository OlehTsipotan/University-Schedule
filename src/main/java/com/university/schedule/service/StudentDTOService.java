package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.StudentDTO;
import com.university.schedule.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentDTOService {

    private final StudentService studentService;

    private final ConverterService converterService;


    public List<StudentDTO> findAll(Pageable pageable){
        return studentService.findAll(pageable).stream().map(this::convert).toList();
    }

    private StudentDTO convert(Student student){
        return converterService.convert(student, StudentDTO.class);
    }
}
