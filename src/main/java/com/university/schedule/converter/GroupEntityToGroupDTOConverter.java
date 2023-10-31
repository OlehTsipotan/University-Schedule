package com.university.schedule.converter;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.model.Course;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public class GroupEntityToGroupDTOConverter implements Converter<Group, GroupDTO> {
    private final ModelMapper modelMapper;

    private final DisciplineEntityToDisciplineDTOConverter disciplineEntityToDisciplineDTOConverter;

    private final CourseEntityToCourseDTOConverter courseEntityToCourseDTOConverter;

    public GroupEntityToGroupDTOConverter() {
        this.modelMapper = new ModelMapper();
        this.disciplineEntityToDisciplineDTOConverter = new DisciplineEntityToDisciplineDTOConverter();
        this.courseEntityToCourseDTOConverter = new CourseEntityToCourseDTOConverter();

        Condition notNull = ctx -> ctx.getSource() != null;

        org.modelmapper.Converter<Discipline, DisciplineDTO> disciplineConverter =
            building -> disciplineEntityToDisciplineDTOConverter.convert(building.getSource());

        org.modelmapper.Converter<Set<Course>, List<CourseDTO>> coursesListConverter =
            courseList -> courseList.getSource().stream().map(courseEntityToCourseDTOConverter::convert).toList();

        modelMapper.typeMap(Group.class, GroupDTO.class).addMappings(modelMapper -> {
            modelMapper.when(notNull).using(disciplineConverter).map(Group::getDiscipline, GroupDTO::setDisciplineDTO);
            modelMapper.when(notNull).using(coursesListConverter).map(Group::getCourses, GroupDTO::setCourseDTOS);
        });
    }

    @Override
    public GroupDTO convert(Group source) {
        return modelMapper.map(source, GroupDTO.class);
    }
}
