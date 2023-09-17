package com.university.schedule.converter;

import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.UserDTO;
import com.university.schedule.model.Role;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToUserDTOConverter implements Converter<User, UserDTO> {

	private final ModelMapper modelMapper;

	private final RoleEntityToRoleDTOConverter roleEntityToRoleDTOConverter;

	public UserEntityToUserDTOConverter() {
        /*
         Need to optimize, cause under User class, can be Student or Teacher (extending User).
         That is why typeMap can ignore User class.
         */
		this.modelMapper = new ModelMapper();
		this.roleEntityToRoleDTOConverter = new RoleEntityToRoleDTOConverter();

		org.modelmapper.Converter<Role, RoleDTO> converter =
				role -> roleEntityToRoleDTOConverter.convert(role.getSource());

		modelMapper.typeMap(User.class, UserDTO.class).addMappings(modelMapper -> {
			modelMapper.map(User::isEnable, UserDTO::setIsEnable);
			modelMapper.using(converter).map(User::getRole, UserDTO::setRoleDTO);
		});
		modelMapper.typeMap(Student.class, UserDTO.class).addMappings(modelMapper -> {
			modelMapper.map(Student::isEnable, UserDTO::setIsEnable);
			modelMapper.using(converter).map(Student::getRole, UserDTO::setRoleDTO);
		});
		modelMapper.typeMap(Teacher.class, UserDTO.class).addMappings(modelMapper -> {
			modelMapper.map(Teacher::isEnable, UserDTO::setIsEnable);
			modelMapper.using(converter).map(Teacher::getRole, UserDTO::setRoleDTO);
		});

	}

	@Override
	public UserDTO convert(User source) {
		return modelMapper.map(source, UserDTO.class);
	}
}