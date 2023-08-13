package com.university.schedule.service;

import com.university.schedule.model.ClassType;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ClassTypeService {

    Long save(ClassType classType);

    ClassType findById(Long id);

    ClassType findByName(String name);

    List<ClassType> findAll();

    List<ClassType> findAll(Sort sort);

    void deleteById(Long id);
}
