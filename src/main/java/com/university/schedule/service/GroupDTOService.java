package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.model.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupDTOService {

    private final GroupService groupService;

    private final ConverterService converterService;

    public List<GroupDTO> findAll(Pageable pageable){
        return groupService.findAll(pageable).stream().map(this::convert).toList();
    }

    private GroupDTO convert(Group group){
        return converterService.convert(group, GroupDTO.class);
    }

}
