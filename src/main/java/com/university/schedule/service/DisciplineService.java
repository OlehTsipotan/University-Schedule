package com.university.schedule.service;

import com.university.schedule.model.Discipline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface DisciplineService {

    List<Discipline> findAll();

    List<Discipline> findAll(Sort sort);

    Page<Discipline> findAll(Pageable pageable);

    Long save(Discipline discipline);

    Discipline findById(Long id);

    void deleteById(Long id);

}
