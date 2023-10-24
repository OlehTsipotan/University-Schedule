package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Building;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.repository.BuildingRepository;
import com.university.schedule.validation.BuildingEntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
public class DefaultBuildingServiceTest {

    private DefaultBuildingService defaultBuildingService;

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private ConverterService converterService;

    @Mock
    private BuildingEntityValidator buildingEntityValidator;

    @BeforeEach
    public void setUp() {
        defaultBuildingService =
            new DefaultBuildingService(buildingRepository, converterService, buildingEntityValidator);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenBuildingIsNull_throwIllegalArgumentException(Building nullBuilding) {
        doThrow(IllegalArgumentException.class).when(buildingEntityValidator).validate(nullBuilding);

        assertThrows(IllegalArgumentException.class, () -> defaultBuildingService.save(nullBuilding));

        verify(buildingEntityValidator).validate(nullBuilding);
        verifyNoInteractions(buildingRepository);
    }

    @Test
    public void save_whenBuildingIsValid_success() {
        Building building = Building.builder().id(1L).name("name").build();
        assertDoesNotThrow(() -> defaultBuildingService.save(building));

        verify(buildingRepository).save(building);
        verify(buildingEntityValidator).validate(building);
    }

    @Test
    public void save_whenBuildingIsNotValid_throwValidationException() {
        Building building = Building.builder().id(1L).name("name").build();
        doThrow(ValidationException.class).when(buildingEntityValidator).validate(building);

        assertThrows(ValidationException.class, () -> defaultBuildingService.save(building));

        verify(buildingEntityValidator).validate(building);
        verifyNoInteractions(buildingRepository);
    }

    @Test
    public void save_whenBuildingRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Building building = Building.builder().id(1L).name("name").build();
        doThrow(BadJpqlGrammarException.class).when(buildingRepository).save(building);

        assertThrows(ServiceException.class, () -> defaultBuildingService.save(building));

        verify(buildingRepository).save(building);
        verify(buildingEntityValidator).validate(building);
    }

    @Test
    public void save_whenBuildingDTOIsValid_success() {
        BuildingDTO buildingDTO = BuildingDTO.builder().id(1L).name("name").address("address").build();
        Building building = Building.builder().id(1L).name("name").address("address").build();
        when(converterService.convert(buildingDTO, Building.class)).thenReturn(building);

        assertDoesNotThrow(() -> defaultBuildingService.save(buildingDTO));

        verify(buildingRepository).save(building);
        verify(buildingEntityValidator).validate(building);
        verify(converterService).convert(buildingDTO, Building.class);
    }

    @Test
    public void save_whenBuildingDTOIsNull_throwIllegalArgumentException() {
        BuildingDTO buildingDTO = BuildingDTO.builder().id(1L).name("name").address("address").build();
        when(converterService.convert(buildingDTO, Building.class)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> defaultBuildingService.save(buildingDTO));

        verifyNoInteractions(buildingEntityValidator);
        verifyNoInteractions(buildingRepository);
        verify(converterService).convert(buildingDTO, Building.class);
    }

    @Test
    public void findByIdAsDTO_whenBuildingIsFound_success() {
        Building building = Building.builder().id(1L).name("name").address("address").build();
        BuildingDTO buildingDTO = BuildingDTO.builder().id(1L).name("name").address("address").build();

        when(buildingRepository.findById(1L)).thenReturn(java.util.Optional.of(building));
        when(converterService.convert(building, BuildingDTO.class)).thenReturn(buildingDTO);

        assertEquals(buildingDTO, defaultBuildingService.findByIdAsDTO(1L));

        verify(buildingRepository).findById(1L);
    }

    @Test
    public void findByIdAsDTO_whenBuildingIsNotFound_throwServiceException() {
        when(buildingRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ServiceException.class, () -> defaultBuildingService.findByIdAsDTO(1L));

        verify(buildingRepository).findById(1L);
    }

    @Test
    public void findByIdAsDTO_whenBuildingRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(buildingRepository.findById(1L)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultBuildingService.findByIdAsDTO(1L));

        verify(buildingRepository).findById(1L);
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDTO_whenIdIsNull_throwServiceException(Long nullId) {
        when(buildingRepository.findById(nullId)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultBuildingService.findByIdAsDTO(nullId));

        verify(buildingRepository).findById(nullId);
    }

    @Test
    public void findByNameAsDTO_whenBuildingIsFound_success() {
        Building building = Building.builder().id(1L).name("name").address("address").build();
        BuildingDTO buildingDTO = BuildingDTO.builder().id(1L).name("name").address("address").build();

        when(buildingRepository.findByName("name")).thenReturn(java.util.Optional.of(building));
        when(converterService.convert(building, BuildingDTO.class)).thenReturn(buildingDTO);

        assertEquals(buildingDTO, defaultBuildingService.findByNameAsDTO("name"));

        verify(buildingRepository).findByName("name");
    }

    @Test
    public void findByNameAsDTO_whenBuildingIsNotFound_throwServiceException() {
        when(buildingRepository.findByName("name")).thenReturn(java.util.Optional.empty());

        assertThrows(ServiceException.class, () -> defaultBuildingService.findByNameAsDTO("name"));

        verify(buildingRepository).findByName("name");
    }

    @Test
    public void findByNameAsDTO_whenBuildingRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(buildingRepository.findByName("name")).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultBuildingService.findByNameAsDTO("name"));

        verify(buildingRepository).findByName("name");
    }

    @Test
    public void findByAddressAsDTO_whenBuildingIsFound_success() {
        Building building = Building.builder().id(1L).name("name").address("address").build();
        BuildingDTO buildingDTO = BuildingDTO.builder().id(1L).name("name").address("address").build();

        when(buildingRepository.findByAddress("address")).thenReturn(java.util.Optional.of(building));
        when(converterService.convert(building, BuildingDTO.class)).thenReturn(buildingDTO);

        assertEquals(buildingDTO, defaultBuildingService.findByAddressAsDTO("address"));

        verify(buildingRepository).findByAddress("address");
    }

    @Test
    public void findByAddressAsDTO_whenBuildingIsNotFound_throwServiceException() {
        when(buildingRepository.findByAddress("address")).thenReturn(java.util.Optional.empty());

        assertThrows(ServiceException.class, () -> defaultBuildingService.findByAddressAsDTO("address"));

        verify(buildingRepository).findByAddress("address");
    }

    @Test
    public void findByAddressAsDTO_whenBuildingRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(buildingRepository.findByAddress("address")).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultBuildingService.findByAddressAsDTO("address"));

        verify(buildingRepository).findByAddress("address");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void findByAddressAsDTO_whenAddressIsNull_throwServiceException(String address) {
        when(buildingRepository.findByAddress(address)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultBuildingService.findByAddressAsDTO(address));

        verify(buildingRepository).findByAddress(address);
    }

    @Test
    public void findAll_success() {
        defaultBuildingService.findAll();
        verify(buildingRepository).findAll();
    }

    @Test
    public void findAll_whenBuildingRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(buildingRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultBuildingService.findAll());

        verify(buildingRepository).findAll();
    }

    @Test
    public void findAllAsDTO_success() {
        defaultBuildingService.findAllAsDTO();
        verify(buildingRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenBuildingRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(buildingRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultBuildingService.findAllAsDTO());

        verify(buildingRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenPageableIsNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> defaultBuildingService.findAllAsDTO(null));

        verifyNoInteractions(buildingRepository);
    }

    @Test
    public void findAllAsDTO_whenPageableIsNotNull_success() {
        Building building = Building.builder().id(1L).name("name").address("address").build();
        BuildingDTO buildingDTO = BuildingDTO.builder().id(1L).name("name").address("address").build();
        Pageable pageable = OffsetBasedPageRequest.of(1, 1);

        when(buildingRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(building)));
        when(converterService.convert(building, BuildingDTO.class)).thenReturn(buildingDTO);

        assertEquals(List.of(buildingDTO), defaultBuildingService.findAllAsDTO(pageable));

        verify(buildingRepository).findAll(any(Pageable.class));
        verify(converterService).convert(building, BuildingDTO.class);
    }

    @Test
    public void deleteById_whenBuildingIsFound_success() {
        when(buildingRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> defaultBuildingService.deleteById(1L));

        verify(buildingRepository).existsById(1L);
        verify(buildingRepository).deleteById(1L);
    }

    @Test
    public void deleteById_whenBuildingIsNotFound_throwServiceException() {
        when(buildingRepository.existsById(1L)).thenReturn(false);

        assertThrows(DeletionFailedException.class, () -> defaultBuildingService.deleteById(1L));

        verify(buildingRepository).existsById(1L);
        verifyNoMoreInteractions(buildingRepository);
    }

    @Test
    public void deleteById_whenBuildingRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(buildingRepository.existsById(1L)).thenReturn(true);
        doThrow(BadJpqlGrammarException.class).when(buildingRepository).deleteById(1L);

        assertThrows(ServiceException.class, () -> defaultBuildingService.deleteById(1L));

        verify(buildingRepository).existsById(1L);
    }
}
