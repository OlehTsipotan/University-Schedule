package com.university.schedule.mapper;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.model.Group;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
public class GroupMapper {
    private final ModelMapper modelMapper;


    public GroupMapper() {
        this.modelMapper = new ModelMapper();
    }

    public GroupDTO convertToDto(Group group) {
        return modelMapper.map(group, GroupDTO.class);
    }
}
