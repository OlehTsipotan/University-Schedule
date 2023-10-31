package com.university.schedule.service;

import com.university.schedule.dto.UserDTO;
import com.university.schedule.model.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    List<User> findAll();

    List<UserDTO> findAllAsDTO();

    List<UserDTO> findAllAsDTO(Pageable pageable);

    Long save(User user);

    Long update(UserDTO userDTO);

    UserDTO findByIdAsDTO(Long id);

    User findByEmail(String email);

    UserDTO findByEmailAsDTO(String email);

    void deleteById(Long id);

}
