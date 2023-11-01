package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ClassType;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.repository.ClassTypeRepository;
import com.university.schedule.validation.ClassTypeEntityValidator;
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
public class DefaultClassTypeServiceTest {

    private DefaultClassTypeService defaultClassTypeService;

    @Mock
    private ClassTypeRepository classTypeRepository;

    @Mock
    private ConverterService converterService;

    @Mock
    private ClassTypeEntityValidator classTypeEntityValidator;

    @BeforeEach
    public void setUp() {
        defaultClassTypeService =
            new DefaultClassTypeService(classTypeRepository, converterService, classTypeEntityValidator);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenClassTypeDTOIsNull_trowIllegalArgumentException(ClassTypeDTO nullClassTypeDTO) {
        when(converterService.convert(nullClassTypeDTO, ClassType.class)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> defaultClassTypeService.save(nullClassTypeDTO));
    }

    @Test
    public void save_whenClassTypeDTOIsValid_returnBuildingId() {
        ClassTypeDTO classTypeDTO = new ClassTypeDTO();
        classTypeDTO.setId(1L);
        ClassType classType = new ClassType();
        classType.setId(1L);

        when(converterService.convert(classTypeDTO, ClassType.class)).thenReturn(classType);
        when(classTypeRepository.save(classType)).thenReturn(classType);

        Long actual = defaultClassTypeService.save(classTypeDTO);

        assertEquals(classType.getId(), actual);
    }

    @Test
    public void save_whenClassTypeRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        ClassTypeDTO classTypeDTO = new ClassTypeDTO();
        classTypeDTO.setId(1L);
        ClassType classType = new ClassType();
        classType.setId(1L);

        when(converterService.convert(classTypeDTO, ClassType.class)).thenReturn(classType);
        when(classTypeRepository.save(classType)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultClassTypeService.save(classTypeDTO));
    }

    @Test
    public void save_whenClassTypeIsValid_success() {
        ClassType classType = new ClassType();
        classType.setId(1L);

        when(classTypeRepository.save(classType)).thenReturn(classType);

        Long actual = defaultClassTypeService.save(classType);

        assertEquals(classType.getId(), actual);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenClassTypeIsNull_throwIllegalArgumentException(ClassType nullClassType) {
        doThrow(IllegalArgumentException.class).when(classTypeEntityValidator).validate(nullClassType);

        assertThrows(IllegalArgumentException.class, () -> defaultClassTypeService.save(nullClassType));
    }

    @Test
    public void save_whenClassTypeIsNotValid_throwValidationException() {
        ClassType classType = new ClassType();
        classType.setId(1L);

        doThrow(ValidationException.class).when(classTypeEntityValidator).validate(classType);

        assertThrows(ValidationException.class, () -> defaultClassTypeService.save(classType));
    }

    @Test
    public void findByIdAsDTO_whenClassTypeRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(classTypeRepository.findById(1L)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultClassTypeService.findByIdAsDTO(1L));
    }

    @Test
    public void findByIdAsDTO_whenClassTypeRepositoryReturnsEmptyOptional_throwServiceException() {
        when(classTypeRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ServiceException.class, () -> defaultClassTypeService.findByIdAsDTO(1L));
    }

    @Test
    public void findByIdAsDTO_whenClassTypeRepositoryReturnsClassType_returnClassTypeDTO() {
        ClassType classType = new ClassType();
        classType.setId(1L);
        ClassTypeDTO classTypeDTO = new ClassTypeDTO();
        classTypeDTO.setId(1L);

        when(classTypeRepository.findById(1L)).thenReturn(java.util.Optional.of(classType));
        when(converterService.convert(classType, ClassTypeDTO.class)).thenReturn(classTypeDTO);

        ClassTypeDTO actual = defaultClassTypeService.findByIdAsDTO(1L);

        assertEquals(classTypeDTO, actual);
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDTO_whenIdIsNull_throwServiceException(Long nullId) {
        when(classTypeRepository.findById(nullId)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultClassTypeService.findByIdAsDTO(nullId));

        verify(classTypeRepository, times(1)).findById(nullId);
    }

    @Test
    public void findByIdAsDTO_success() {
        ClassType classType = new ClassType();
        classType.setId(1L);
        ClassTypeDTO classTypeDTO = new ClassTypeDTO();
        classTypeDTO.setId(1L);

        when(classTypeRepository.findById(1L)).thenReturn(java.util.Optional.of(classType));
        when(converterService.convert(classType, ClassTypeDTO.class)).thenReturn(classTypeDTO);

        ClassTypeDTO actual = defaultClassTypeService.findByIdAsDTO(1L);

        assertEquals(classTypeDTO, actual);
    }

    @ParameterizedTest
    @NullSource
    public void findByName_whenNameIsNull_throwServiceException(String nullName) {
        when(classTypeRepository.findByName(nullName)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultClassTypeService.findByName(nullName));

        verify(classTypeRepository, times(1)).findByName(nullName);
    }

    @Test
    public void findByName_whenClassTypeRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(classTypeRepository.findByName("name")).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultClassTypeService.findByName("name"));
    }

    @Test
    public void findByName_whenClassTypeNotFound_throwServiceException() {
        when(classTypeRepository.findByName("name")).thenReturn(java.util.Optional.empty());

        assertThrows(ServiceException.class, () -> defaultClassTypeService.findByName("name"));
    }

    @Test
    public void findByName_success() {
        ClassType classType = new ClassType();
        classType.setId(1L);
        classType.setName("name");

        when(classTypeRepository.findByName("name")).thenReturn(java.util.Optional.of(classType));

        ClassType actual = defaultClassTypeService.findByName("name");

        assertEquals(classType, actual);
        verify(classTypeRepository, times(1)).findByName("name");
    }

    @Test
    public void findAllAsDTO_whenClassTypeRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(classTypeRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultClassTypeService.findAllAsDTO());
    }

    @Test
    public void findAllAsDTO_success() {
        ClassType classType = new ClassType();
        classType.setId(1L);
        ClassTypeDTO classTypeDTO = new ClassTypeDTO();
        classTypeDTO.setId(1L);

        when(classTypeRepository.findAll()).thenReturn(java.util.List.of(classType));
        when(converterService.convert(classType, ClassTypeDTO.class)).thenReturn(classTypeDTO);

        List<ClassTypeDTO> actual = defaultClassTypeService.findAllAsDTO();

        assertEquals(java.util.List.of(classTypeDTO), actual);
    }


    @Test
    public void findAllAsDTO_whenPageableIsValid_success() {
        ClassType classType = new ClassType();
        classType.setId(1L);
        ClassTypeDTO classTypeDTO = new ClassTypeDTO();
        classTypeDTO.setId(1L);

        Pageable pageable = OffsetBasedPageRequest.of(1, 1);

        when(classTypeRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(classType)));
        when(converterService.convert(classType, ClassTypeDTO.class)).thenReturn(classTypeDTO);

        List<ClassTypeDTO> actual = defaultClassTypeService.findAllAsDTO(pageable);

        assertEquals(java.util.List.of(classTypeDTO), actual);
    }

    @ParameterizedTest
    @NullSource
    public void findAllAsDTO_whenPageableIsNull_success(Pageable nullPageable) {
        assertThrows(IllegalArgumentException.class, () -> defaultClassTypeService.findAllAsDTO(nullPageable));
    }

    @Test
    public void deleteById_whenClassTypeIsFound_success() {
        when(classTypeRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> defaultClassTypeService.deleteById(1L));

        verify(classTypeRepository).existsById(1L);
        verify(classTypeRepository).deleteById(1L);
    }

    @Test
    public void deleteById_whenClassTypeIsNotFound_throwDeletionFailedException() {
        when(classTypeRepository.existsById(1L)).thenReturn(false);

        assertThrows(DeletionFailedException.class, () -> defaultClassTypeService.deleteById(1L));

        verify(classTypeRepository).existsById(1L);
        verifyNoMoreInteractions(classTypeRepository);
    }

    @Test
    public void deleteById_whenClassTypeRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(classTypeRepository.existsById(1L)).thenReturn(true);
        doThrow(BadJpqlGrammarException.class).when(classTypeRepository).deleteById(1L);

        assertThrows(ServiceException.class, () -> defaultClassTypeService.deleteById(1L));

        verify(classTypeRepository).existsById(1L);
    }

}
