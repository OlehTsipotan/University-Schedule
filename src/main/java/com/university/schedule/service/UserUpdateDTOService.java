package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.UserUpdateDTO;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserUpdateDTOService {

    private final UserService userService;

    private final ConverterService converterService;


    // TODO: need to optimize
    public Long save(UserUpdateDTO userUpdateDTO) {

        User foundedUser = userService.findById(userUpdateDTO.getId());

        User userToSave = convertToUserEntity(userUpdateDTO);

        if (foundedUser instanceof Student){
            Student student = convertToStudentEntity(userUpdateDTO);
            student.setGroup(((Student) foundedUser).getGroup());
            userToSave = student;
        } else if (foundedUser instanceof Teacher){
            Teacher teacher = convertToTeacherEntity(userUpdateDTO);
            teacher.setCourses(((Teacher) foundedUser).getCourses());
            userToSave = teacher;
        }

        userToSave.setPassword(foundedUser.getPassword());

        return userService.save(userToSave);
    }

    public UserUpdateDTO findById(Long id){
        User user = userService.findById(id);
        return this.convertToUserUpdateDTO(user);
    }

    private User convertToUserEntity(UserUpdateDTO userUpdateDTO){
        return converterService.convert(userUpdateDTO, User.class);
    }

    private Teacher convertToTeacherEntity(UserUpdateDTO userUpdateDTO){
        return converterService.convert(userUpdateDTO, Teacher.class);
    }

    private Student convertToStudentEntity(UserUpdateDTO userUpdateDTO){
        return converterService.convert(userUpdateDTO, Student.class);
    }

    private UserUpdateDTO convertToUserUpdateDTO(User user){
        return converterService.convert(user, UserUpdateDTO.class);
    }
}
