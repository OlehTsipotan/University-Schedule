package com.university.schedule.service;

import com.university.schedule.model.ClassType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ClassTypeService {

    Long save(ClassType classType);

    ClassType findById(Long id);

    ClassType findByName(String name);

    List<ClassType> findAll();

    List<ClassType> findAll(Sort sort);

    Page<ClassType> findAll(Pageable pageable);

    void deleteById(Long id);
}
