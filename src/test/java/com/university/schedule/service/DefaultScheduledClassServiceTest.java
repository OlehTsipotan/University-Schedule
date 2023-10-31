package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.ScheduleFilterItem;
import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ScheduledClass;
import com.university.schedule.repository.ScheduledClassRepository;
import com.university.schedule.validation.ScheduledClassEntityValidator;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class DefaultScheduledClassServiceTest {

    private DefaultScheduledClassService defaultScheduledClassService;

    @Mock
    private ScheduledClassRepository scheduledClassRepository;

    @Mock
    private ScheduleFilterItemService scheduleFilterItemService;

    @Mock
    private ConverterService converterService;

    @Mock
    private ScheduledClassEntityValidator scheduledClassEntityValidator;

    @BeforeEach
    public void setUp() {
        defaultScheduledClassService =
            new DefaultScheduledClassService(scheduledClassRepository, scheduleFilterItemService, converterService,
                scheduledClassEntityValidator);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenScheduledClassIsNull_throwIllegalArgumentException(ScheduledClass nullScheduledClass) {
        doThrow(IllegalArgumentException.class).when(scheduledClassEntityValidator).validate(nullScheduledClass);

        assertThrows(IllegalArgumentException.class, () -> defaultScheduledClassService.save(nullScheduledClass));

        verify(scheduledClassEntityValidator).validate(nullScheduledClass);
    }

    @Test
    public void save_success() {
        ScheduledClass scheduledClass = new ScheduledClass();

        assertDoesNotThrow(() -> defaultScheduledClassService.save(scheduledClass));

        verify(scheduledClassEntityValidator).validate(scheduledClass);
        verify(scheduledClassRepository).save(scheduledClass);
    }

    @Test
    public void save_whenScheduledClassRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        ScheduledClass scheduledClass = new ScheduledClass();
        doThrow(BadJpqlGrammarException.class).when(scheduledClassRepository).save(scheduledClass);

        assertThrows(ServiceException.class, () -> defaultScheduledClassService.save(scheduledClass));

        verify(scheduledClassEntityValidator).validate(scheduledClass);
        verify(scheduledClassRepository).save(scheduledClass);
    }

    @Test
    public void save_whenScheduledClassIsInvalid_throwValidationException() {
        ScheduledClass scheduledClass = new ScheduledClass();
        doThrow(ValidationException.class).when(scheduledClassEntityValidator).validate(scheduledClass);

        assertThrows(ValidationException.class, () -> defaultScheduledClassService.save(scheduledClass));

        verify(scheduledClassEntityValidator).validate(scheduledClass);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenScheduledClassDTOIsNull_throwIllegalArgumentException(
        ScheduledClassDTO nullScheduledClassDTO) {
        when(converterService.convert(nullScheduledClassDTO, ScheduledClass.class)).thenThrow(
            IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> defaultScheduledClassService.save(nullScheduledClassDTO));

        verify(converterService).convert(nullScheduledClassDTO, ScheduledClass.class);
    }

    @Test
    public void save_whenScheduledClassDTO_success() {
        ScheduledClass scheduledClass = new ScheduledClass();
        ScheduledClassDTO scheduledClassDTO = new ScheduledClassDTO();

        when(converterService.convert(scheduledClassDTO, ScheduledClass.class)).thenReturn(scheduledClass);

        assertDoesNotThrow(() -> defaultScheduledClassService.save(scheduledClassDTO));

        verify(scheduledClassEntityValidator).validate(scheduledClass);
        verify(scheduledClassRepository).save(scheduledClass);
    }

    @Test
    public void saveScheduledClassDTO_whenScheduledClassRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        ScheduledClass scheduledClass = new ScheduledClass();
        ScheduledClassDTO scheduledClassDTO = new ScheduledClassDTO();
        when(converterService.convert(scheduledClassDTO, ScheduledClass.class)).thenReturn(scheduledClass);
        doThrow(BadJpqlGrammarException.class).when(scheduledClassRepository).save(scheduledClass);

        assertThrows(ServiceException.class, () -> defaultScheduledClassService.save(scheduledClassDTO));

        verify(scheduledClassEntityValidator).validate(scheduledClass);
        verify(scheduledClassRepository).save(scheduledClass);
    }

    @Test
    public void findByIdAsDTO_success() {
        ScheduledClass scheduledClass = new ScheduledClass();
        ScheduledClassDTO scheduledClassDTO = new ScheduledClassDTO();
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.of(scheduledClass));
        when(converterService.convert(scheduledClass, ScheduledClassDTO.class)).thenReturn(scheduledClassDTO);

        assertDoesNotThrow(() -> defaultScheduledClassService.findByIdAsDTO(1L));

        verify(scheduledClassRepository).findById(1L);
        verify(converterService).convert(scheduledClass, ScheduledClassDTO.class);
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDTO_whenIdIsNull_throwServiceException(Long nullId) {
        when(scheduledClassRepository.findById(nullId)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultScheduledClassService.findByIdAsDTO(nullId));
    }

    @Test
    public void findByIdAsDTO_whenScheduledClassNotFound_throwServiceException() {
        when(scheduledClassRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ServiceException.class, () -> defaultScheduledClassService.findByIdAsDTO(1L));
    }

    @Test
    public void findAll_success() {
        ScheduledClass scheduledClass = new ScheduledClass();
        when(scheduledClassRepository.findAll()).thenReturn(List.of(scheduledClass));
        assertEquals(List.of(scheduledClass), defaultScheduledClassService.findAll());
        verify(scheduledClassRepository).findAll();
    }

    @Test
    public void findAll_whenScheduledClassRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(scheduledClassRepository.findAll()).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultScheduledClassService.findAll());
    }

    @Test
    public void findAllAsDTO_success() {
        ScheduledClass scheduledClass = new ScheduledClass();
        ScheduledClassDTO scheduledClassDTO = new ScheduledClassDTO();
        when(scheduledClassRepository.findAll()).thenReturn(List.of(scheduledClass));
        when(converterService.convert(scheduledClass, ScheduledClassDTO.class)).thenReturn(scheduledClassDTO);
        assertEquals(List.of(scheduledClassDTO), defaultScheduledClassService.findAllAsDTO());
        verify(scheduledClassRepository).findAll();
        verify(converterService).convert(scheduledClass, ScheduledClassDTO.class);
    }

    @Test
    public void findAllAsDTO_whenScheduledClassRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(scheduledClassRepository.findAll()).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultScheduledClassService.findAllAsDTO());
    }

    @ParameterizedTest
    @NullSource
    public void findAllAsDTO_whenPageableIsNull_throwIllegalArgumentException(Pageable nullPageable) {
        assertThrows(IllegalArgumentException.class, () -> defaultScheduledClassService.findAllAsDTO(nullPageable));
    }

    @Test
    public void findAllAsDTO_whenPageableIsValid_success() {
        ScheduledClass scheduledClass = new ScheduledClass();
        ScheduledClassDTO scheduledClassDTO = new ScheduledClassDTO();
        Pageable pageable = mock(Pageable.class);
        when(scheduledClassRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(scheduledClass)));
        when(converterService.convert(scheduledClass, ScheduledClassDTO.class)).thenReturn(scheduledClassDTO);
        assertEquals(List.of(scheduledClassDTO), defaultScheduledClassService.findAllAsDTO(pageable));
        verify(scheduledClassRepository).findAll(pageable);
        verify(converterService).convert(scheduledClass, ScheduledClassDTO.class);
    }

    @ParameterizedTest
    @NullSource
    public void findAllAsDTOByScheduleFilterItem_whenScheduleFilterItemIsNull_throwIllegalArgumentException(
        ScheduleFilterItem nullScheduleFilterItem) {
        doThrow(IllegalArgumentException.class).when(scheduleFilterItemService).processRawItem(nullScheduleFilterItem);
        assertThrows(IllegalArgumentException.class,
            () -> defaultScheduledClassService.findAllAsDTOByScheduleFilterItem(nullScheduleFilterItem));
    }

    @Test
    public void findAllAsDTOByScheduleFilterItem_whenGroupIdListIsNull_success() {
        ScheduledClass scheduledClass = new ScheduledClass();
        ScheduledClassDTO scheduledClassDTO = new ScheduledClassDTO();
        ScheduleFilterItem scheduleFilterItem = new ScheduleFilterItem();

        when(
            scheduledClassRepository.findAllFiltered(scheduleFilterItem.getStartDate(), scheduleFilterItem.getEndDate(),
                scheduleFilterItem.getClassTypeId(), scheduleFilterItem.getTeacherId())).thenReturn(
            List.of(scheduledClass));

        when(converterService.convert(scheduledClass, ScheduledClassDTO.class)).thenReturn(scheduledClassDTO);

        assertEquals(List.of(scheduledClassDTO),
            defaultScheduledClassService.findAllAsDTOByScheduleFilterItem(scheduleFilterItem));

        verify(scheduledClassRepository).findAllFiltered(scheduleFilterItem.getStartDate(),
            scheduleFilterItem.getEndDate(), scheduleFilterItem.getClassTypeId(), scheduleFilterItem.getTeacherId());
        verify(converterService).convert(scheduledClass, ScheduledClassDTO.class);
    }

    @Test
    public void findAllAsDTOByScheduleFilterItem_whenGroupIdListIsNotNull_success() {
        ScheduledClass scheduledClass = new ScheduledClass();
        ScheduledClassDTO scheduledClassDTO = new ScheduledClassDTO();
        ScheduleFilterItem scheduleFilterItem = new ScheduleFilterItem();
        scheduleFilterItem.setGroupIdList(List.of(1L));

        when(
            scheduledClassRepository.findAllFiltered(scheduleFilterItem.getStartDate(), scheduleFilterItem.getEndDate(),
                scheduleFilterItem.getClassTypeId(), scheduleFilterItem.getTeacherId(),
                scheduleFilterItem.getGroupIdList())).thenReturn(List.of(scheduledClass));

        when(converterService.convert(scheduledClass, ScheduledClassDTO.class)).thenReturn(scheduledClassDTO);

        assertEquals(List.of(scheduledClassDTO),
            defaultScheduledClassService.findAllAsDTOByScheduleFilterItem(scheduleFilterItem));

        verify(scheduledClassRepository).findAllFiltered(scheduleFilterItem.getStartDate(),
            scheduleFilterItem.getEndDate(), scheduleFilterItem.getClassTypeId(), scheduleFilterItem.getTeacherId(),
            scheduleFilterItem.getGroupIdList());
        verify(converterService).convert(scheduledClass, ScheduledClassDTO.class);
    }

    @Test
    public void deleteById_whenScheduledClassRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(InvalidDataAccessApiUsageException.class).when(scheduledClassRepository).deleteById(1L);
        assertThrows(ServiceException.class, () -> defaultScheduledClassService.deleteById(1L));
    }

    @Test
    public void deleteById_whenScheduledClassDoNotExists_throwServiceException() {
        when(scheduledClassRepository.existsById(1L)).thenReturn(false);
        assertThrows(DeletionFailedException.class, () -> defaultScheduledClassService.deleteById(1L));
        verify(scheduledClassRepository).existsById(1L);
    }

    @Test
    public void deleteById_success() {
        when(scheduledClassRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> defaultScheduledClassService.deleteById(1L));
        verify(scheduledClassRepository).existsById(1L);
        verify(scheduledClassRepository).deleteById(1L);
    }
}
