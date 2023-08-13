package com.university.schedule.repository;

import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByDiscipline(Discipline discipline);
}
