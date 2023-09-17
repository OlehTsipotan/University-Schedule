package com.university.schedule.converter;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.model.Course;
import com.university.schedule.model.Group;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GroupDTOToGroupEntityConverter implements Converter<GroupDTO, Group> {
	private final ModelMapper modelMapper;
	private final CourseDTOToCourseEntityConverter courseDTOToCourseEntityConverter;


	public GroupDTOToGroupEntityConverter() {
		this.modelMapper = new ModelMapper();
		this.courseDTOToCourseEntityConverter = new CourseDTOToCourseEntityConverter();

		org.modelmapper.Converter<List<CourseDTO>, Set<Course>> coursesListConverter =
				courseDTOSet -> courseDTOSet.getSource().stream().map(courseDTOToCourseEntityConverter::convert)
						.collect(Collectors.toSet());

		modelMapper.typeMap(GroupDTO.class, Group.class).addMappings(modelMapper -> {
			modelMapper.using(coursesListConverter).map(GroupDTO::getCourseDTOS, Group::setCourses);
		});
	}

	@Override
	public Group convert(GroupDTO source) {
		return modelMapper.map(source, Group.class);
	}
}
