package com.university.schedule.service;

import com.university.schedule.dto.RoleDTO;
import com.university.schedule.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {

	List<Role> findAll();

	List<RoleDTO> findAllAsDTO();

	List<RoleDTO> findAllAsDTO(Pageable pageable);

	Role findByName(String name);

	Long save(Role role);

	Long save(RoleDTO roleDTO);

	Role findById(Long id);

	RoleDTO findByIdAsDTO(Long id);

	void deleteById(Long id);
}
