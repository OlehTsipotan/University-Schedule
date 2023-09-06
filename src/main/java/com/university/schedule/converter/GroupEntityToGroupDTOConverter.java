package com.university.schedule.converter;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.model.Course;
import com.university.schedule.model.Group;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public class GroupEntityToGroupDTOConverter implements Converter<Group, GroupDTO> {
    private final ModelMapper modelMapper;


    public GroupEntityToGroupDTOConverter() {
        this.modelMapper = new ModelMapper();
        org.modelmapper.Converter<Set<Course>, List<String>> converter = c -> c.getSource().stream().map(Course::getName).toList();
        modelMapper.typeMap(Group.class, GroupDTO.class).addMappings(
                modelMapper -> modelMapper.using(converter)
                        .map(Group::getCourses, GroupDTO::setCourseNames));
    }

    @Override
    public GroupDTO convert(Group source) {
        return modelMapper.map(source, GroupDTO.class);
    }
}
