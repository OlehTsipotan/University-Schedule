package com.university.schedule.converter;

import com.university.schedule.dto.UserDTO;
import com.university.schedule.model.Student;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class UserDTOToStudentEntityConverter implements Converter<UserDTO, Student> {

	private final ModelMapper modelMapper;

	public UserDTOToStudentEntityConverter() {
		this.modelMapper = new ModelMapper();
		modelMapper.typeMap(UserDTO.class, Student.class)
				.addMappings(modelMapper -> modelMapper.map(UserDTO::isEnable, Student::setIsEnable));
	}

	@Override
	public Student convert(UserDTO source) {
		return modelMapper.map(source, Student.class);
	}
}
