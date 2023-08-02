package com.university.schedule.service;

import com.university.schedule.model.Group;

import java.util.List;

public interface GroupService {

    List<Group> findAll();

    Long save(String groupName);

    Long save(Group group);

    Group findById(Long id);

    void deleteById(Long id);

    boolean assignToCourse(Long groupId, Long courseId);

    boolean removeFromCourse(Long groupId, Long courseId);

}
