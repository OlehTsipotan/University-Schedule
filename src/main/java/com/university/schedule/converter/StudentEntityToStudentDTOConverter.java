package com.university.schedule.converter;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.StudentDTO;
import com.university.schedule.model.Group;
import com.university.schedule.model.Role;
import com.university.schedule.model.Student;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentEntityToStudentDTOConverter implements Converter<Student, StudentDTO> {

	private final ModelMapper modelMapper;
	private final RoleEntityToRoleDTOConverter roleEntityToRoleDTOConverter;
	private final GroupEntityToGroupDTOConverter groupEntityToGroupDTOConverter;

	public StudentEntityToStudentDTOConverter() {
		this.modelMapper = new ModelMapper();
		this.roleEntityToRoleDTOConverter = new RoleEntityToRoleDTOConverter();
		this.groupEntityToGroupDTOConverter = new GroupEntityToGroupDTOConverter();

		org.modelmapper.Converter<Role, RoleDTO> roleDTOConverter =
				role -> roleEntityToRoleDTOConverter.convert(role.getSource());

		org.modelmapper.Converter<Group, GroupDTO> groupDTOConverter =
				group -> groupEntityToGroupDTOConverter.convert(group.getSource());

		modelMapper.typeMap(Student.class, StudentDTO.class).addMappings(modelMapper -> {
			modelMapper.map(Student::isEnable, StudentDTO::setIsEnable);
			modelMapper.using(roleDTOConverter).map(Student::getRole, StudentDTO::setRoleDTO);
			modelMapper.using(groupDTOConverter).map(Student::getGroup, StudentDTO::setGroupDTO);
		});
	}

	@Override
	public StudentDTO convert(Student source) {
		return modelMapper.map(source, StudentDTO.class);
	}
}
