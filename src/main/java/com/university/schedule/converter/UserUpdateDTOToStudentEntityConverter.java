package com.university.schedule.converter;

import com.university.schedule.dto.UserUpdateDTO;
import com.university.schedule.model.Student;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class UserUpdateDTOToStudentEntityConverter implements Converter<UserUpdateDTO, Student> {

    private final ModelMapper modelMapper;

    public UserUpdateDTOToStudentEntityConverter() {
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(UserUpdateDTO.class, Student.class).addMappings(modelMapper ->
                modelMapper.map(UserUpdateDTO::isEnable, Student::setIsEnable));
    }

    @Override
    public Student convert(UserUpdateDTO source) {
        return modelMapper.map(source, Student.class);
    }
}
