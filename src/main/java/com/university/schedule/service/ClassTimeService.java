package com.university.schedule.service;

import com.university.schedule.model.ClassTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface ClassTimeService {

    Long save(ClassTime classTime);

    ClassTime findById(Long id);

    ClassTime findByOrderNumber(Integer order);

    List<ClassTime> findAll();

    Page<ClassTime> findAll(Pageable pageable);

    List<ClassTime> findAll(Sort sort);

    void deleteById(Long id);
}
