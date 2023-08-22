package com.university.schedule.service;

import com.university.schedule.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {

    List<Role> findAll();

    Page<Role> findAll(Pageable pageable);

    Role findByName(String name);

    Long save(Role role);

    Role findById(Long id);

    void deleteById(Long id);
}
