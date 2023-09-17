package com.university.schedule.converter;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.model.Course;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class CourseEntityToCourseDTOConverter implements Converter<Course, CourseDTO> {

	private final ModelMapper modelMapper;


	public CourseEntityToCourseDTOConverter() {
		this.modelMapper = new ModelMapper();
	}

	@Override
	public CourseDTO convert(Course source) {
		return modelMapper.map(source, CourseDTO.class);
	}
}