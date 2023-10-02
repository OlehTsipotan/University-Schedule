package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import com.university.schedule.repository.GroupRepository;
import com.university.schedule.validation.GroupEntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class DefaultGroupService implements GroupService {

	private final GroupRepository groupRepository;

	private final ConverterService converterService;

	private final GroupEntityValidator groupEntityValidator;

	@Override
	public List<Group> findAll() throws ServiceException {
		List<Group> groups = execute(() -> groupRepository.findAll());
		log.debug("Retrieved All {} Groups", groups.size());
		return groups;
	}

	@Override
	public List<GroupDTO> findAllAsDTO() throws ServiceException {
		List<GroupDTO> groupDTOList =
				execute(() -> groupRepository.findAll()).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Groups", groupDTOList.size());
		return groupDTOList;
	}

	@Override
	public List<GroupDTO> findAllAsDTO(Pageable pageable) {
		List<GroupDTO> groupDTOSList =
				execute(() -> groupRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Groups", groupDTOSList.size());
		return groupDTOSList;
	}

	@Override
	@Transactional
	public Long save(Group group) {
		execute(() -> {
			groupEntityValidator.validate(group);
			groupRepository.save(group);
		});
		log.info("saved {}", group);
		return group.getId();
	}

	@Override
	@Transactional
	public Long save(GroupDTO groupDTO) {
		Group group = convertToEntity(groupDTO);
		group.getCourses().forEach(course -> log.info(course.toString()));
		execute(() -> {
			groupEntityValidator.validate(group);
			groupRepository.save(group);
		});
		log.info("saved {}", group);
		return group.getId();
	}

	private Group findById(Long id) {
		Group group =
				execute(() -> groupRepository.findById(id)).orElseThrow(() -> new ServiceException("Group not found"));
		log.debug("Retrieved {}", group);
		return group;
	}

	@Override
	public GroupDTO findByIdAsDTO(Long id) {
		Group group =
				execute(() -> groupRepository.findById(id)).orElseThrow(() -> new ServiceException("Group not found"));
		log.debug("Retrieved {}", group);
		return convertToDTO(group);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		try {
			findById(id);
		} catch (ServiceException e) {
			throw new DeletionFailedException("There is no Group to delete with id = " + id);
		}
		execute(() -> groupRepository.deleteById(id));
		log.info("Deleted id = {}", id);
	}

	@Override
	public List<Group> findByDiscipline(Discipline discipline) {
		List<Group> groups = execute(() -> groupRepository.findByDiscipline(discipline));
		log.debug("Retrieved All {} Groups", groups.size());
		return groups;
	}

	private GroupDTO convertToDTO(Group source) {
		return converterService.convert(source, GroupDTO.class);
	}

	private Group convertToEntity(GroupDTO groupDTO) {
		return converterService.convert(groupDTO, Group.class);
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
