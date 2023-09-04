package com.university.schedule.mapper;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.Course;
import com.university.schedule.model.Group;
import com.university.schedule.model.Teacher;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public class GroupMapper {
    private final ModelMapper modelMapper;


    public GroupMapper() {
        this.modelMapper = new ModelMapper();
        Converter<Set<Course>, List<String>> converter = c -> c.getSource().stream().map(Course::getName).toList();
        modelMapper.typeMap(Group.class, GroupDTO.class).addMappings(
                modelMapper -> modelMapper.using(converter)
                        .map(Group::getCourses, GroupDTO::setCourseNames));
    }

    public GroupDTO convertToDto(Group group) {
        return modelMapper.map(group, GroupDTO.class);
    }
}
