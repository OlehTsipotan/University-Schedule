package com.university.schedule.service;

import com.university.schedule.model.Discipline;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface DisciplineService {

    List<Discipline> findAll();

    List<Discipline> findAll(Sort sort);

    Long save(Discipline discipline);

    Discipline findById(Long id);

    void deleteById(Long id);

}
