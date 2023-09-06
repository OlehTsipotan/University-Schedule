package com.university.schedule.converter;

import com.university.schedule.dto.UserUpdateDTO;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToUserUpdateDTOConverter implements Converter<User, UserUpdateDTO> {

    private final ModelMapper modelMapper;

    public UserEntityToUserUpdateDTOConverter() {
        /*
         Need to optimize, cause under User class, can be Student or Teacher (extending User).
         That is why typeMap can ignore User class.
         */
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(User.class, UserUpdateDTO.class).addMappings(
                modelMapper ->
                        modelMapper.map(User::isEnable, UserUpdateDTO::setIsEnable));
        modelMapper.typeMap(Teacher.class, UserUpdateDTO.class).addMappings(
                modelMapper ->
                        modelMapper.map(Teacher::isEnable, UserUpdateDTO::setIsEnable));
        modelMapper.typeMap(Student.class, UserUpdateDTO.class).addMappings(
                modelMapper ->
                        modelMapper.map(Student::isEnable, UserUpdateDTO::setIsEnable));
    }

    @Override
    public UserUpdateDTO convert(User source) {
        return modelMapper.map(source, UserUpdateDTO.class);
    }
}