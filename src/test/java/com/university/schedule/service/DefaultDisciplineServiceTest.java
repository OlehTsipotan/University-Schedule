package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Discipline;
import com.university.schedule.repository.DisciplineRepository;
import com.university.schedule.validation.DisciplineEntityValidator;
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
public class DefaultDisciplineServiceTest {

    private DefaultDisciplineService defaultDisciplineService;

    @Mock
    private DisciplineRepository disciplineRepository;

    @Mock
    private DisciplineEntityValidator disciplineEntityValidator;

    @Mock
    private ConverterService converterService;

    @BeforeEach
    public void setUp() {
        defaultDisciplineService =
            new DefaultDisciplineService(disciplineRepository, disciplineEntityValidator, converterService);
    }

    @Test
    public void findAll_success() {
        Discipline discipline = new Discipline();
        when(disciplineRepository.findAll()).thenReturn(List.of(discipline));

        assertEquals(List.of(discipline), defaultDisciplineService.findAll());

        verify(disciplineRepository).findAll();
    }

    @Test
    public void findAll_whenDisciplineRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(disciplineRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultDisciplineService.findAll());

        verify(disciplineRepository).findAll();
    }

    @Test
    public void findAllAsDTO_success() {
        Discipline discipline = new Discipline();
        DisciplineDTO disciplineDTO = new DisciplineDTO();
        when(converterService.convert(discipline, DisciplineDTO.class)).thenReturn(disciplineDTO);
        when(disciplineRepository.findAll()).thenReturn(List.of(discipline));

        assertEquals(List.of(disciplineDTO), defaultDisciplineService.findAllAsDTO());

        verify(disciplineRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenDisciplineRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(disciplineRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultDisciplineService.findAllAsDTO());

        verify(disciplineRepository).findAll();
    }

    @ParameterizedTest
    @NullSource
    public void findAllAsDTO_whenPageableIsNull_throwIllegalArgumentException(Pageable pageable) {
        assertThrows(IllegalArgumentException.class, () -> defaultDisciplineService.findAllAsDTO(pageable));
    }

    @Test
    public void findAllAsDTO_whenPageableIsValid_success() {
        Discipline discipline = new Discipline();
        DisciplineDTO disciplineDTO = new DisciplineDTO();
        Pageable pageable = Pageable.ofSize(1);
        when(converterService.convert(discipline, DisciplineDTO.class)).thenReturn(disciplineDTO);
        when(disciplineRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(discipline)));

        assertEquals(List.of(disciplineDTO), defaultDisciplineService.findAllAsDTO(pageable));

        verify(disciplineRepository).findAll(pageable);
    }

    @Test
    public void findAllAsDTO_whenPageableIsValidAndDisciplineRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Pageable pageable = Pageable.ofSize(1);
        when(disciplineRepository.findAll(pageable)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultDisciplineService.findAllAsDTO(pageable));

        verify(disciplineRepository).findAll(pageable);
    }

    @Test
    public void save_whenDisciplineIsValid_success() {
        Discipline discipline = new Discipline();
        when(disciplineRepository.save(discipline)).thenReturn(discipline);

        assertEquals(discipline.getId(), defaultDisciplineService.save(discipline));

        verify(disciplineRepository).save(discipline);
    }

    @Test
    public void save_whenDisciplineIsInvalid_throwValidation() {
        Discipline discipline = new Discipline();
        doThrow(ValidationException.class).when(disciplineEntityValidator).validate(discipline);

        assertThrows(ValidationException.class, () -> defaultDisciplineService.save(discipline));

        verify(disciplineEntityValidator).validate(discipline);
        verifyNoInteractions(disciplineRepository);
    }

    @Test
    public void save_whenDisciplineRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Discipline discipline = new Discipline();
        doThrow(BadJpqlGrammarException.class).when(disciplineRepository).save(discipline);

        assertThrows(ServiceException.class, () -> defaultDisciplineService.save(discipline));

        verify(disciplineRepository).save(discipline);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenDisciplineDTOIsNull_throwIllegalArgumentException(Discipline discipline) {
        doThrow(IllegalArgumentException.class).when(disciplineEntityValidator).validate(null);
        assertThrows(IllegalArgumentException.class, () -> defaultDisciplineService.save(discipline));

        verify(disciplineEntityValidator).validate(null);
    }

    @Test
    public void save_whenDisciplineDTOIsInvalid_throwValidationException() {
        Discipline discipline = new Discipline();
        doThrow(ValidationException.class).when(disciplineEntityValidator).validate(discipline);
        assertThrows(ValidationException.class, () -> defaultDisciplineService.save(discipline));

        verify(disciplineEntityValidator).validate(discipline);
        verifyNoInteractions(disciplineRepository);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenDisciplineDTOIsNull_throwIllegalArgumentException(DisciplineDTO disciplineDTO) {
        when(converterService.convert(disciplineDTO, Discipline.class)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> defaultDisciplineService.save(disciplineDTO));

        verify(converterService).convert(disciplineDTO, Discipline.class);
    }

    @Test
    public void save_whenDisciplineDTOIsValid_success() {
        DisciplineDTO disciplineDTO = new DisciplineDTO();
        Discipline discipline = new Discipline();
        when(converterService.convert(disciplineDTO, Discipline.class)).thenReturn(discipline);
        when(disciplineRepository.save(discipline)).thenReturn(discipline);

        assertEquals(discipline.getId(), defaultDisciplineService.save(disciplineDTO));

        verify(disciplineRepository).save(discipline);
    }

    @Test
    public void findByIdAsDTO_whenDisciplineRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(disciplineRepository.findById(anyLong())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultDisciplineService.findByIdAsDTO(anyLong()));

        verify(disciplineRepository).findById(anyLong());
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDTO_whenIdIsNull_throwServiceException(Long nullId) {
        when(disciplineRepository.findById(null)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultDisciplineService.findByIdAsDTO(nullId));
    }

    @Test
    public void findByIdAsDTO_success(){
        Discipline discipline = new Discipline();
        DisciplineDTO disciplineDTO = new DisciplineDTO();
        when(disciplineRepository.findById(anyLong())).thenReturn(java.util.Optional.of(discipline));
        when(converterService.convert(discipline, DisciplineDTO.class)).thenReturn(disciplineDTO);

        assertEquals(disciplineDTO, defaultDisciplineService.findByIdAsDTO(anyLong()));

        verify(disciplineRepository).findById(anyLong());
        verify(converterService).convert(discipline, DisciplineDTO.class);
    }

    @Test
    public void findByIdAsDTO_whenNoDisciplineFound_throwServiceException(){
        when(disciplineRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(ServiceException.class, () -> defaultDisciplineService.findByIdAsDTO(anyLong()));

        verify(disciplineRepository).findById(anyLong());
    }

    @Test
    public void delete_whenDisciplineRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(disciplineRepository).existsById(anyLong());

        assertThrows(ServiceException.class, () -> defaultDisciplineService.deleteById(anyLong()));

        verify(disciplineRepository).existsById(anyLong());
    }

    @Test
    public void delete_whenNoDisciplineFound_throwServiceException(){
        when(disciplineRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ServiceException.class, () -> defaultDisciplineService.deleteById(anyLong()));

        verify(disciplineRepository).existsById(anyLong());
        verifyNoMoreInteractions(disciplineRepository);
    }

    @Test
    public void delete_success(){
        when(disciplineRepository.existsById(anyLong())).thenReturn(true);
        assertDoesNotThrow(() -> defaultDisciplineService.deleteById(1L));

        verify(disciplineRepository).existsById(anyLong());
        verify(disciplineRepository).deleteById(anyLong());
    }

}
