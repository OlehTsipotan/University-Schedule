package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.model.ClassTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassTimeDTOService {

    private final ClassTimeService classTimeService;

    private final ConverterService converterService;

    public List<ClassTimeDTO> findAll(Pageable pageable) {
         return classTimeService.findAll(pageable).stream().map(this::convert).toList();
    }

    public List<ClassTimeDTO> findAll(){
        return classTimeService.findAll().stream().map(this::convert).toList();
    }

    private ClassTimeDTO convert(ClassTime classTime){
        return converterService.convert(classTime, ClassTimeDTO.class);
    }
}
