package com.university.schedule.converter;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.Course;
import com.university.schedule.model.Role;
import com.university.schedule.model.Teacher;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class TeacherEntityToTeacherDTOConverter implements Converter<Teacher, TeacherDTO> {

	private final ModelMapper modelMapper;
	private final CourseEntityToCourseDTOConverter courseEntityToCourseDTOConverter;
	private final RoleEntityToRoleDTOConverter roleEntityToRoleDTOConverter;


	public TeacherEntityToTeacherDTOConverter() {
		this.modelMapper = new ModelMapper();
		this.courseEntityToCourseDTOConverter = new CourseEntityToCourseDTOConverter();
		this.roleEntityToRoleDTOConverter = new RoleEntityToRoleDTOConverter();


		org.modelmapper.Converter<Role, RoleDTO> roleDTOConverter =
				role -> roleEntityToRoleDTOConverter.convert(role.getSource());

		org.modelmapper.Converter<Set<Course>, List<CourseDTO>> courseDTOSConverter =
				courseList -> courseList.getSource().stream().map(courseEntityToCourseDTOConverter::convert).toList();

		modelMapper.typeMap(Teacher.class, TeacherDTO.class).addMappings(modelMapper -> {
			modelMapper.map(Teacher::isEnable, TeacherDTO::setIsEnable);
			modelMapper.using(roleDTOConverter).map(Teacher::getRole, TeacherDTO::setRoleDTO);
			modelMapper.using(courseDTOSConverter).map(Teacher::getCourses, TeacherDTO::setCourseDTOS);
		});

	}

	@Override
	public TeacherDTO convert(Teacher source) {
		return modelMapper.map(source, TeacherDTO.class);
	}
}
