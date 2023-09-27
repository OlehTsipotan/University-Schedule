package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Role;
import com.university.schedule.repository.RoleRepository;
import com.university.schedule.validation.RoleEntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class DefaultRoleService implements RoleService {


	private final RoleRepository roleRepository;

	private final ConverterService converterService;

	private final RoleEntityValidator roleEntityValidator;

	@Override
	public List<Role> findAll() {
		List<Role> roles = execute(() -> roleRepository.findAll());
		log.debug("Retrieved All {} Role", roles.size());
		return roles;
	}

	@Override
	public List<RoleDTO> findAllAsDTO() {
		List<RoleDTO> roleDTOList = execute(() -> roleRepository.findAll()).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Role", roleDTOList.size());
		return roleDTOList;
	}

	@Override
	public List<RoleDTO> findAllForRegistrationAsDTO() {
		List<RoleDTO> roleDTOList =
				execute(() -> roleRepository.findAll()).stream().map(this::convertToDTO).filter(roleDTO ->
								!"Admin".equals(roleDTO.getName())).toList();
		log.debug("Retrieved All {} Role", roleDTOList.size());
		return roleDTOList;
	}

	@Override
	public List<RoleDTO> findAllAsDTO(Pageable pageable) {
		List<RoleDTO> roleDTOS =
				execute(() -> roleRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Role", roleDTOS.size());
		return roleDTOS;
	}

	@Override
	@Transactional
	public Long save(Role role) {
		execute(() -> {
			roleEntityValidator.validate(role);
			roleRepository.save(role);
		});
		log.info("saved {}", role);
		return role.getId();
	}

	@Override
	@Transactional
	public Long save(RoleDTO roleDTO) {
		Role role = convertToEntity(roleDTO);
		log.info("SERVICE: " + role.getAuthorities().toString());
		execute(() -> {
			roleEntityValidator.validate(role);
			roleRepository.save(role);
		});
		log.info("saved {}", role);
		return role.getId();
	}

	@Override
	public Role findById(Long id) {
		Role role =
				execute(() -> roleRepository.findById(id)).orElseThrow(() -> new ServiceException("Role not found"));
		log.debug("Retrieved {}", role);
		return role;
	}

	@Override
	public RoleDTO findByIdAsDTO(Long id) {
		Role role =
				execute(() -> roleRepository.findById(id)).orElseThrow(() -> new ServiceException("Role not found"));
		log.debug("Retrieved {}", role);
		return convertToDTO(role);
	}

	@Override
	public Role findByName(String name) {
		Role role = execute(() -> roleRepository.findByName(name)).orElseThrow(
				() -> new ServiceException("Role not found"));
		log.debug("Retrieved {}", role);
		return role;
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		try {
			findById(id);
		} catch (ServiceException e) {
			throw new DeletionFailedException("There is no Role to delete with id = " + id);
		}
		execute(() -> roleRepository.deleteById(id));
		log.info("Deleted id = {}", id);
	}

	private RoleDTO convertToDTO(Role source) {
		return converterService.convert(source, RoleDTO.class);
	}

	private Role convertToEntity(RoleDTO source) {
		return converterService.convert(source, Role.class);
	}

	private <T> T execute(DaoSupplier<T> supplier) {
		try {
			return supplier.get();
		} catch (DataAccessException e) {
			throw new ServiceException("DAO operation failed", e);
		}
	}

	private void execute(DaoProcessor processor) {
		try {
			processor.process();
		} catch (DataAccessException e) {
			throw new ServiceException("DAO operation failed", e);
		}
	}

	@FunctionalInterface
	public interface DaoSupplier<T> {
		T get();
	}

	@FunctionalInterface
	public interface DaoProcessor {
		void process();
	}
}
