package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import com.university.schedule.repository.GroupRepository;
import com.university.schedule.validation.GroupEntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.BadJpqlGrammarException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class DefaultGroupServiceTest {

    private DefaultGroupService defaultGroupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupEntityValidator groupEntityValidator;

    @Mock
    private ConverterService converterService;

    @BeforeEach
    public void setUp() {
        defaultGroupService = new DefaultGroupService(groupRepository, groupEntityValidator, converterService);
    }

    @Test
    public void findAll_success() {
        Group group = new Group();
        when(groupRepository.findAll()).thenReturn(List.of(group));

        assertEquals(List.of(group), defaultGroupService.findAll());

        verify(groupRepository).findAll();
    }

    @Test
    public void findAll_whenRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(groupRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultGroupService.findAll());

        verify(groupRepository).findAll();
    }

    @Test
    public void findAllAsDTO_success() {
        Group group = new Group();
        when(groupRepository.findAll()).thenReturn(List.of(group));

        GroupDTO groupDTO = new GroupDTO();
        when(converterService.convert(group, GroupDTO.class)).thenReturn(groupDTO);

        assertEquals(List.of(groupDTO), defaultGroupService.findAllAsDTO());

        verify(groupRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(groupRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultGroupService.findAllAsDTO());

        verify(groupRepository).findAll();
    }

    @Test
    public void findAllAsDTOPageable_whenRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Pageable pageable = Pageable.unpaged();
        when(groupRepository.findAll(pageable)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultGroupService.findAllAsDTO(pageable));

        verify(groupRepository).findAll(pageable);
    }

    @ParameterizedTest
    @NullSource
    public void findAllAsDTOPageable_whenPageableIsNull_throwIllegalArgumentException(Pageable pageable) {
        assertThrows(IllegalArgumentException.class, () -> defaultGroupService.findAllAsDTO(pageable));
    }

    @Test
    public void findAllAsDTOPageable_success() {
        Pageable pageable = Pageable.unpaged();
        Group group = new Group();
        when(groupRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(group)));

        GroupDTO groupDTO = new GroupDTO();
        when(converterService.convert(group, GroupDTO.class)).thenReturn(groupDTO);

        assertEquals(List.of(groupDTO), defaultGroupService.findAllAsDTO(pageable));

        verify(groupRepository).findAll(pageable);
    }

    @Test
    public void save_success() {
        Group group = new Group();
        when(groupRepository.save(group)).thenReturn(group);

        assertEquals(group.getId(), defaultGroupService.save(group));

        verify(groupRepository).save(group);
    }

    @Test
    public void save_whenRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Group group = new Group();
        when(groupRepository.save(group)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultGroupService.save(group));

        verify(groupRepository).save(group);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenGroupIsNull_throwServiceException(Group group) {
        when(groupRepository.save(null)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultGroupService.save(group));

        verify(groupRepository).save(null);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenGroupDTOIsNull_throwIllegalArgumentException(GroupDTO nullGroupDTO) {
        when(converterService.convert(nullGroupDTO, Group.class)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> defaultGroupService.save(nullGroupDTO));
    }

    @Test
    public void save_whenGroupDTOIsNotValid_throwValidationException() {
        GroupDTO groupDTO = new GroupDTO();
        Group group = new Group();
        when(converterService.convert(groupDTO, Group.class)).thenReturn(group);
        doThrow(ValidationException.class).when(groupEntityValidator).validate(group);

        assertThrows(ValidationException.class, () -> defaultGroupService.save(groupDTO));

        verify(groupEntityValidator).validate(group);
        verify(converterService).convert(groupDTO, Group.class);
    }

    @Test
    public void save_whenGroupIsNotValid_throwValidationException() {
        Group group = new Group();
        doThrow(ValidationException.class).when(groupEntityValidator).validate(group);

        assertThrows(ValidationException.class, () -> defaultGroupService.save(group));

        verify(groupEntityValidator).validate(group);
        verifyNoInteractions(groupRepository);
    }

    @Test
    public void save_whenGroupDTOIsValid_success(){
        GroupDTO groupDTO = new GroupDTO();
        Group group = new Group();
        when(converterService.convert(groupDTO, Group.class)).thenReturn(group);
        when(groupRepository.save(group)).thenReturn(group);

        assertEquals(group.getId(), defaultGroupService.save(groupDTO));

        verify(groupEntityValidator).validate(group);
        verify(converterService).convert(groupDTO, Group.class);
        verify(groupRepository).save(group);
    }

    @Test
    public void deleteById_whenRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(groupRepository).existsById(1L);

        assertThrows(ServiceException.class, () -> defaultGroupService.deleteById(1L));

        verify(groupRepository).existsById(1L);
    }

    @Test
    public void deleteById_whenGroupIsNotPresent_throwServiceException() {
        when(groupRepository.existsById(1L)).thenReturn(false);

        assertThrows(DeletionFailedException.class, () -> defaultGroupService.deleteById(1L));

        verify(groupRepository).existsById(1L);
    }

    @Test
    public void deleteById_success() {
        when(groupRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> defaultGroupService.deleteById(1L));

        verify(groupRepository).deleteById(1L);
    }

    @Test
    public void findByIdAsDTO_whenRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(groupRepository.findById(1L)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultGroupService.findByIdAsDTO(1L));

        verify(groupRepository).findById(1L);
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDTO_whenIdIsNull_throwServiceException(Long nullId) {
        when(groupRepository.findById(nullId)).thenThrow(InvalidDataAccessApiUsageException.class);

        assertThrows(ServiceException.class, () -> defaultGroupService.findByIdAsDTO(nullId));
    }

    @Test
    public void findByIdAsDTO_whenGroupNotFound_throwServiceException() {
        when(groupRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ServiceException.class, () -> defaultGroupService.findByIdAsDTO(1L));

        verify(groupRepository).findById(1L);
    }

    @Test
    public void findByIdAsDTO_success() {
        Group group = new Group();
        when(groupRepository.findById(1L)).thenReturn(java.util.Optional.of(group));

        GroupDTO groupDTO = new GroupDTO();
        when(converterService.convert(group, GroupDTO.class)).thenReturn(groupDTO);

        assertEquals(groupDTO, defaultGroupService.findByIdAsDTO(1L));

        verify(groupRepository).findById(1L);
    }

    @Test
    public void findByDiscipline_whenRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(groupRepository.findByDiscipline(any())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultGroupService.findByDiscipline(new Discipline()));

        verify(groupRepository).findByDiscipline(any());
    }

    @ParameterizedTest
    @NullSource
    public void findByDiscipline_whenDisciplineIsNull_throwServiceException(Discipline discipline) {
        when(groupRepository.findByDiscipline(discipline)).thenThrow(InvalidDataAccessApiUsageException.class);

        assertThrows(ServiceException.class, () -> defaultGroupService.findByDiscipline(discipline));

        verify(groupRepository).findByDiscipline(discipline);
    }

    @Test
    public void findByDiscipline_success() {
        Discipline discipline = new Discipline();
        Group group = new Group();
        when(groupRepository.findByDiscipline(discipline)).thenReturn(List.of(group));

        assertEquals(List.of(group), defaultGroupService.findByDiscipline(discipline));

        verify(groupRepository).findByDiscipline(discipline);
    }

}
