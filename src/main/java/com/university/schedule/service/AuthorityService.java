package com.university.schedule.service;

import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.model.Authority;
import com.university.schedule.model.Role;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorityService {

	Long save(Authority authority);

	Authority findById(Long id);

	AuthorityDTO findByIdAsDTO(Long id);

	Authority findByName(String name);

	List<Authority> findByRole(Role role);

	List<Authority> findAll();

	List<AuthorityDTO> findAllAsDTO();

	List<AuthorityDTO> findAllAsDTO(Pageable pageable);

	void deleteById(Long id);
}
