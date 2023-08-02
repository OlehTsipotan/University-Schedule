package com.university.schedule.service;

import com.university.schedule.model.ClassTime;

import java.util.List;
import java.util.Optional;

public interface ClassTimeService {

    Long save(ClassTime classTime);

    ClassTime findById(Long id);

    ClassTime findByOrderNumber(Integer order);

    List<ClassTime> findAll();

    void deleteById(Long id);
}
