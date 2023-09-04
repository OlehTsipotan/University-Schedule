package com.university.schedule.service;

import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface GroupService {

    List<Group> findAll();

    Page<Group> findAll(Pageable pageable);

    List<Group> findAll(Sort sort);

    Long save(Group group);

    Group findById(Long id);

    void deleteById(Long id);

    List<Group> findByDiscipline(Discipline discipline);

    Group findByName(String name);

    boolean assignToCourse(Long groupId, Long courseId);

    boolean removeFromCourse(Long groupId, Long courseId);

}
