package com.university.schedule.service;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupService {

    List<Group> findAll();

    List<GroupDTO> findAllAsDTO();

    List<GroupDTO> findAllAsDTO(Pageable pageable);

    Long save(Group group);

    Long save(GroupDTO groupDTO);

    List<Group> findByDiscipline(Discipline discipline);

    GroupDTO findByIdAsDTO(Long id);

    void deleteById(Long id);


}
