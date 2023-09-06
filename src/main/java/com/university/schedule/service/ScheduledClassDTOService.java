package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.model.ScheduledClass;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledClassDTOService {

    private final ScheduledClassService scheduledClassService;

    private final ConverterService converterService;

    public List<ScheduledClassDTO> findAll(Pageable pageable){
        return scheduledClassService.findAll(pageable).stream().map(this::convert).toList();
    }


    private ScheduledClassDTO convert(ScheduledClass scheduledClass){
        return converterService.convert(scheduledClass, ScheduledClassDTO.class);
    }
}
