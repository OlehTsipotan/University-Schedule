package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ClassTimeUpdateDTO;
import com.university.schedule.model.ClassTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassTimeUpdateDTOService {

    private final ClassTimeService classTimeService;

    private final ConverterService converterService;

    public Long save(ClassTimeUpdateDTO classTimeUpdateDTO) {

        ClassTime classTime = convertToUserEntity(classTimeUpdateDTO);
        return classTimeService.save(classTime);
    }

    public ClassTimeUpdateDTO findById(Long id){
        ClassTime classTime = classTimeService.findById(id);
        return this.convertToUserUpdateDTO(classTime);
    }

    private ClassTimeUpdateDTO convertToUserUpdateDTO(ClassTime classTime){
        return converterService.convert(classTime, ClassTimeUpdateDTO.class);
    }

    private ClassTime convertToUserEntity(ClassTimeUpdateDTO classTimeUpdateDTO){
        return converterService.convert(classTimeUpdateDTO, ClassTime.class);
    }
}