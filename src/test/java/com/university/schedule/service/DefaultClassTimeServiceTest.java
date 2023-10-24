package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ClassTime;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.repository.ClassTimeRepository;
import com.university.schedule.validation.ClassTimeEntityValidator;
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
public class DefaultClassTimeServiceTest {

    private DefaultClassTimeService defaultClassTimeService;

    @Mock
    private ClassTimeRepository classTimeRepository;

    @Mock
    private ConverterService converterService;

    @Mock
    private ClassTimeEntityValidator classTimeEntityValidator;

    @BeforeEach
    public void setUp() {
        defaultClassTimeService =
            new DefaultClassTimeService(classTimeRepository, classTimeEntityValidator, converterService);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenClassTimeIsNull_thenThrowsIllegalArgumentException(ClassTime nullClassTime) {
        doThrow(IllegalArgumentException.class).when(classTimeEntityValidator).validate(nullClassTime);

        assertThrows(IllegalArgumentException.class, () -> defaultClassTimeService.save(nullClassTime));

        verify(classTimeEntityValidator).validate(nullClassTime);
        verifyNoInteractions(classTimeRepository);
    }

    @Test
    public void save_whenClassTimeIsValid_success() {
        ClassTime classTime = new ClassTime();
        assertDoesNotThrow(() -> defaultClassTimeService.save(classTime));

        verify(classTimeRepository).save(classTime);
        verify(classTimeEntityValidator).validate(classTime);
    }

    @Test
    public void save_whenClassTimeIsNotValid_throwValidationException() {
        ClassTime classTime = new ClassTime();
        doThrow(ValidationException.class).when(classTimeEntityValidator).validate(classTime);

        assertThrows(ValidationException.class, () -> defaultClassTimeService.save(classTime));

        verify(classTimeEntityValidator).validate(classTime);
        verifyNoInteractions(classTimeRepository);
    }

    @Test
    public void save_whenClassTimeRepositoryThrowsExceptionExtendsDataAccessException_thenThrowsServiceException() {
        ClassTime classTime = new ClassTime();
        doThrow(BadJpqlGrammarException.class).when(classTimeRepository).save(classTime);

        assertThrows(RuntimeException.class, () -> defaultClassTimeService.save(classTime));

        verify(classTimeRepository).save(classTime);
        verify(classTimeEntityValidator).validate(classTime);
    }

    @Test
    public void save_whenClassTimeDTOIsValid_success() {
        ClassTimeDTO classTimeDTO = new ClassTimeDTO();
        ClassTime classTime = new ClassTime();
        when(converterService.convert(classTimeDTO, ClassTime.class)).thenReturn(classTime);

        assertDoesNotThrow(() -> defaultClassTimeService.save(classTimeDTO));

        verify(classTimeRepository).save(classTime);
        verify(classTimeEntityValidator).validate(classTime);
        verify(converterService).convert(classTimeDTO, ClassTime.class);
    }

    @Test
    public void save_whenClassTimeDTOIsNotValid_throwValidationException() {
        ClassTimeDTO classTimeDTO = new ClassTimeDTO();
        ClassTime classTime = new ClassTime();
        when(converterService.convert(classTimeDTO, ClassTime.class)).thenReturn(classTime);
        doThrow(ValidationException.class).when(classTimeEntityValidator).validate(classTime);

        assertThrows(ValidationException.class, () -> defaultClassTimeService.save(classTimeDTO));

        verify(classTimeEntityValidator).validate(classTime);
        verifyNoInteractions(classTimeRepository);
        verify(converterService).convert(classTimeDTO, ClassTime.class);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenClassTimeDTOIsNull_thenThrowsIllegalArgumentException(ClassTimeDTO nullClassTimeDTO) {
        when(converterService.convert(nullClassTimeDTO, ClassTime.class)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> defaultClassTimeService.save(nullClassTimeDTO));

        verify(converterService).convert(nullClassTimeDTO, ClassTime.class);
        verifyNoInteractions(classTimeRepository);
        verifyNoInteractions(classTimeEntityValidator);
    }

    @Test
    public void findByIdAsDTO_whenClassTimeIsFound_success() {
        ClassTime classTime = new ClassTime();
        ClassTimeDTO classTimeDTO = new ClassTimeDTO();
        when(classTimeRepository.findById(1L)).thenReturn(java.util.Optional.of(classTime));
        when(converterService.convert(classTime, ClassTimeDTO.class)).thenReturn(classTimeDTO);

        assertDoesNotThrow(() -> defaultClassTimeService.findByIdAsDTO(1L));

        verify(classTimeRepository).findById(1L);
        verify(converterService).convert(classTime, ClassTimeDTO.class);
    }

    @Test
    public void findByIdAsDTO_whenClassTimeIsNotFound_throwServiceException() {
        when(classTimeRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ServiceException.class, () -> defaultClassTimeService.findByIdAsDTO(1L));

        verify(classTimeRepository).findById(1L);
        verifyNoInteractions(converterService);
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDTO_whenIdIsNull_throwServiceException(Long nullId) {
        when(classTimeRepository.findById(nullId)).thenThrow(InvalidDataAccessApiUsageException.class);

        assertThrows(ServiceException.class, () -> defaultClassTimeService.findByIdAsDTO(nullId));

        verify(classTimeRepository).findById(nullId);
        verifyNoInteractions(converterService);
    }

    @Test
    public void findByIdAsDTO_whenClassTimeRepositoryThrowsExceptionExtendsDataAccessException_throwsServiceException() {
        when(classTimeRepository.findById(1L)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultClassTimeService.findByIdAsDTO(1L));

        verify(classTimeRepository).findById(1L);
        verifyNoInteractions(converterService);
    }

    @ParameterizedTest
    @NullSource
    public void findByOrderNumber_whenOrderNumberIsNull_throwServiceException(Integer nullOrderNumber) {
        when(classTimeRepository.findByOrderNumber(nullOrderNumber)).thenThrow(
            InvalidDataAccessApiUsageException.class);

        assertThrows(ServiceException.class, () -> defaultClassTimeService.findByOrderNumber(nullOrderNumber));

        verify(classTimeRepository).findByOrderNumber(nullOrderNumber);
        verifyNoMoreInteractions(classTimeRepository);
    }

    @Test
    public void findByOrderNumber_whenClassTimeRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(classTimeRepository.findByOrderNumber(any())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultClassTimeService.findByOrderNumber(1));

        verify(classTimeRepository).findByOrderNumber(1);
        verifyNoMoreInteractions(classTimeRepository);
    }

    @Test
    public void findByOrderNumber_whenClassTimeNotFound_throwServiceException() {
        when(classTimeRepository.findByOrderNumber(1)).thenReturn(java.util.Optional.empty());

        assertThrows(ServiceException.class, () -> defaultClassTimeService.findByOrderNumber(1));

        verify(classTimeRepository).findByOrderNumber(1);
        verifyNoMoreInteractions(classTimeRepository);
    }

    @Test
    public void findByOrderNumber_whenClassTimeIsFound_throwServiceException() {
        ClassTime classTime = new ClassTime();
        when(classTimeRepository.findByOrderNumber(1)).thenReturn(java.util.Optional.of(classTime));

        assertEquals(classTime, defaultClassTimeService.findByOrderNumber(1));

        verify(classTimeRepository).findByOrderNumber(1);
        verifyNoMoreInteractions(classTimeRepository);
    }

    @ParameterizedTest
    @NullSource
    public void findAllAsDTO_whenPageableIsNull_throwIllegalArgumentException(Pageable nullPageable) {
        assertThrows(IllegalArgumentException.class, () -> defaultClassTimeService.findAllAsDTO(nullPageable));
    }

    @Test
    public void findAllAsDTO_whenPageableIsValid_success() {
        ClassTime classTime = new ClassTime();
        ClassTimeDTO classTimeDTO = new ClassTimeDTO();
        Pageable pageable = OffsetBasedPageRequest.of(1, 1);

        when(classTimeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(classTime)));
        when(converterService.convert(classTime, ClassTimeDTO.class)).thenReturn(classTimeDTO);

        assertEquals(List.of(classTimeDTO), defaultClassTimeService.findAllAsDTO(pageable));

        verify(classTimeRepository).findAll(any(Pageable.class));
        verify(converterService).convert(classTime, ClassTimeDTO.class);
    }

    @Test
    public void findAllAsDTO_whenClassTimeRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(classTimeRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultClassTimeService.findAllAsDTO());

        verify(classTimeRepository).findAll();
    }

    @Test
    public void findAllAsDTO_success() {
        assertDoesNotThrow(() -> defaultClassTimeService.findAllAsDTO());
        verify(classTimeRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenBuildingRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(classTimeRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultClassTimeService.findAllAsDTO());

        verify(classTimeRepository).findAll();
    }

    @Test
    public void deleteById_whenClassTimeIsFound_success() {
        when(classTimeRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> defaultClassTimeService.deleteById(1L));

        verify(classTimeRepository).existsById(1L);
        verify(classTimeRepository).deleteById(1L);
    }

    @Test
    public void deleteById_whenBuildingIsNotFound_throwServiceException() {
        when(classTimeRepository.existsById(1L)).thenReturn(false);

        assertThrows(DeletionFailedException.class, () -> defaultClassTimeService.deleteById(1L));

        verify(classTimeRepository).existsById(1L);
        verifyNoMoreInteractions(classTimeRepository);
    }

    @Test
    public void deleteById_whenBuildingRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(classTimeRepository.existsById(1L)).thenReturn(true);
        doThrow(BadJpqlGrammarException.class).when(classTimeRepository).deleteById(1L);

        assertThrows(ServiceException.class, () -> defaultClassTimeService.deleteById(1L));

        verify(classTimeRepository).existsById(1L);
    }


}
