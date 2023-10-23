package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.repository.ClassroomRepository;
import com.university.schedule.validation.ClassroomEntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.dao.DuplicateKeyException;
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
public class DefaultClassroomServiceTest {

    private DefaultClassroomService defaultClassroomService;

    @Mock
    private ClassroomRepository classroomRepository;

    @Mock
    private ClassroomEntityValidator classroomEntityValidator;

    @Mock
    private ConverterService converterService;

    @BeforeEach
    public void setUp() {
        defaultClassroomService =
            new DefaultClassroomService(classroomRepository, classroomEntityValidator, converterService);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenClassroomIsNull_thenIllegalArgumentException(Classroom nullClassroom) {
        doThrow(IllegalArgumentException.class).when(classroomEntityValidator).validate(nullClassroom);
        assertThrows(IllegalArgumentException.class, () -> defaultClassroomService.save(nullClassroom));

        verify(classroomEntityValidator).validate(nullClassroom);
        verifyNoInteractions(classroomRepository);
    }

    @Test
    public void save_whenClassroomIsValid_success() {
        Classroom classroom = new Classroom();
        doNothing().when(classroomEntityValidator).validate(classroom);
        when(classroomRepository.save(classroom)).thenReturn(classroom);

        assertDoesNotThrow(() -> defaultClassroomService.save(classroom));

        verify(classroomEntityValidator).validate(classroom);
        verify(classroomRepository).save(classroom);
    }

    @Test
    public void save_whenClassroomIsNotValid_throwValidationException() {
        Classroom classroom = new Classroom();
        doThrow(ValidationException.class).when(classroomEntityValidator).validate(classroom);
        assertThrows(ValidationException.class, () -> defaultClassroomService.save(classroom));

        verify(classroomEntityValidator).validate(classroom);
        verifyNoInteractions(classroomRepository);
    }

    @Test
    public void save_whenClassroomRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Classroom classroom = Classroom.builder().id(1L).name("name").build();
        when(classroomRepository.save(classroom)).thenThrow(DuplicateKeyException.class);
        assertThrows(ServiceException.class, () -> defaultClassroomService.save(classroom));

        verify(classroomEntityValidator).validate(classroom);
        verify(classroomRepository).save(classroom);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenClassroomDTOIsNull_thenIllegalArgumentException(ClassroomDTO nullClassroomDTO) {
        when(converterService.convert(nullClassroomDTO, Classroom.class)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> defaultClassroomService.save((nullClassroomDTO)));
        verifyNoInteractions(classroomRepository);
    }

    @Test
    public void save_whenClassroomDTOIsValid_success() {
        ClassroomDTO classroomDTO = new ClassroomDTO();
        Classroom classroom = new Classroom();
        when(converterService.convert(classroomDTO, Classroom.class)).thenReturn(classroom);
        doNothing().when(classroomEntityValidator).validate(classroom);
        when(classroomRepository.save(classroom)).thenReturn(classroom);

        assertDoesNotThrow(() -> defaultClassroomService.save(classroomDTO));

        verify(converterService).convert(classroomDTO, Classroom.class);
        verify(classroomEntityValidator).validate(classroom);
        verify(classroomRepository).save(classroom);
    }

    @Test
    public void findByIdAsDTO_whenClassroomIsNotFound_throwServiceException() {
        when(classroomRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ServiceException.class, () -> defaultClassroomService.findByIdAsDTO(1L));
        verify(classroomRepository).findById(1L);
    }

    @Test
    public void findByIdAsDTO_whenClassroomIsFound_success() {
        Classroom classroom = new Classroom();
        ClassroomDTO classroomDTO = new ClassroomDTO();
        when(classroomRepository.findById(1L)).thenReturn(Optional.of(classroom));
        when(converterService.convert(classroom, ClassroomDTO.class)).thenReturn(classroomDTO);
        assertDoesNotThrow(() -> defaultClassroomService.findByIdAsDTO(1L));
        verify(classroomRepository).findById(1L);
        verify(converterService).convert(classroom, ClassroomDTO.class);
    }

    @Test
    public void findByIdAsDTO_whenClassroomRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(classroomRepository.findById(1L)).thenThrow(BadJpqlGrammarException.class);
        assertThrows(ServiceException.class, () -> defaultClassroomService.findByIdAsDTO(1L));
        verify(classroomRepository).findById(1L);
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDTO_whenIdIsNull_throwServiceException(Long nullId) {
        when(classroomRepository.findById(nullId)).thenThrow(InvalidDataAccessApiUsageException.class);

        assertThrows(ServiceException.class, () -> defaultClassroomService.findByIdAsDTO(nullId));
        verify(classroomRepository).findById(nullId);
        verifyNoMoreInteractions(classroomRepository);
    }

    @ParameterizedTest
    @NullSource
    public void findByBuilding_whenBuildingIsNull_throwServiceException(Building nullBuilding) {
        when(classroomRepository.findByBuilding(nullBuilding)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultClassroomService.findByBuilding(nullBuilding));
        verify(classroomRepository).findByBuilding(nullBuilding);
        verifyNoMoreInteractions(classroomRepository);
    }

    @Test
    public void findByBuilding_whenBuildingIsValid_success() {
        Building building = new Building();
        Classroom classroomToBeFounded = new Classroom();

        when(classroomRepository.findByBuilding(building)).thenReturn(List.of(classroomToBeFounded));
        assertEquals(List.of(classroomToBeFounded), defaultClassroomService.findByBuilding(building));
        verify(classroomRepository).findByBuilding(building);
    }

    @Test
    public void findByBuilding_whenClassroomRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Building building = new Building();
        when(classroomRepository.findByBuilding(building)).thenThrow(BadJpqlGrammarException.class);

        when(classroomRepository.findByBuilding(any())).thenThrow(BadJpqlGrammarException.class);
        assertThrows(ServiceException.class, () -> defaultClassroomService.findByBuilding(building));
        verify(classroomRepository).findByBuilding(building);
    }

    @Test
    public void findAllAsDTO_whenClassroomRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(classroomRepository.findAll()).thenThrow(BadJpqlGrammarException.class);
        assertThrows(ServiceException.class, () -> defaultClassroomService.findAllAsDTO());
        verify(classroomRepository).findAll();
    }

    @Test
    public void findAllAsDTO_success(){
        Classroom classroom = new Classroom();
        ClassroomDTO classroomDTO = new ClassroomDTO();
        when(classroomRepository.findAll()).thenReturn(List.of(classroom));
        when(converterService.convert(classroom, ClassroomDTO.class)).thenReturn(classroomDTO);
        assertEquals(List.of(classroomDTO), defaultClassroomService.findAllAsDTO());
        verify(classroomRepository).findAll();
        verify(converterService).convert(classroom, ClassroomDTO.class);
    }

    @Test
    public void findAllAsDTO_whenPageableIsNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> defaultClassroomService.findAllAsDTO(null));
        verifyNoInteractions(classroomRepository);
    }

    @Test
    public void findAllAsDTO_whenPageableIsValid_success() {
        Building building = Building.builder().id(1L).name("name").address("address").build();
        Classroom classroom = Classroom.builder().id(1L).name("name").building(building).build();

        BuildingDTO buildingDTO = BuildingDTO.builder().id(1L).name("name").address("address").build();
        ClassroomDTO classroomDTO = ClassroomDTO.builder().id(1L).name("name").buildingDTO(buildingDTO).build();

        Pageable pageable = OffsetBasedPageRequest.of(1, 1);

        when(classroomRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(classroom)));
        when(converterService.convert(classroom, ClassroomDTO.class)).thenReturn(classroomDTO);

        assertEquals(List.of(classroomDTO), defaultClassroomService.findAllAsDTO(pageable));

        verify(classroomRepository).findAll(any(Pageable.class));
        verify(converterService).convert(classroom, ClassroomDTO.class);
    }

    @Test
    public void deleteById_whenClassroomIsNotFound_throwServiceException() {
        when(classroomRepository.existsById(1L)).thenReturn(false);
        assertThrows(ServiceException.class, () -> defaultClassroomService.deleteById(1L));
        verify(classroomRepository).existsById(1L);
    }

    @Test
    public void deleteById_whenClassroomIsFound_success() {
        when(classroomRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> defaultClassroomService.deleteById(1L));
        verify(classroomRepository).existsById(1L);
        verify(classroomRepository).deleteById(1L);
    }

    @Test
    public void deleteById_whenClassroomRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(classroomRepository.existsById(1L)).thenThrow(BadJpqlGrammarException.class);
        assertThrows(ServiceException.class, () -> defaultClassroomService.deleteById(1L));
        verify(classroomRepository).existsById(1L);
    }
}
