package com.university.schedule.repository;

import com.university.schedule.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Group save(Group group);

    List<Group> findAll();

    Optional<Group> findById(Long id);

    void deleteById(Long id);
}
