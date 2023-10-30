package com.university.schedule.converter;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.Course;
import com.university.schedule.model.Teacher;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class TeacherDTOToTeacherEntityConverter implements Converter<TeacherDTO, Teacher> {

    private final ModelMapper modelMapper;
    private final CourseDTOToCourseEntityConverter courseDTOToCourseEntityConverter;

    public TeacherDTOToTeacherEntityConverter() {
        this.modelMapper = new ModelMapper();
        this.courseDTOToCourseEntityConverter = new CourseDTOToCourseEntityConverter();

        org.modelmapper.Converter<List<CourseDTO>, Set<Course>> coursesListConverter =
            courseDTOSet -> courseDTOSet.getSource().stream().map(courseDTOToCourseEntityConverter::convert)
                .collect(Collectors.toSet());

        Condition notNull = ctx -> ctx.getSource() != null;

        modelMapper.typeMap(TeacherDTO.class, Teacher.class).addMappings(modelMapper -> {
            modelMapper.when(notNull).using(coursesListConverter).map(TeacherDTO::getCourseDTOS, Teacher::setCourses);
            modelMapper.map(TeacherDTO::isEnable, Teacher::setIsEnable);
        });
    }

    @Override
    public Teacher convert(TeacherDTO source) {
        return modelMapper.map(source, Teacher.class);
    }
}