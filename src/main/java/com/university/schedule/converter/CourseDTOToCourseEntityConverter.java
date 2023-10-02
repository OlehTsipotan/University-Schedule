package com.university.schedule.converter;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.model.Course;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class CourseDTOToCourseEntityConverter implements Converter<CourseDTO, Course> {

	private final ModelMapper modelMapper;


	public CourseDTOToCourseEntityConverter() {
		this.modelMapper = new ModelMapper();
	}

	@Override
	public Course convert(CourseDTO source) {
		return modelMapper.map(source, Course.class);
	}
}
