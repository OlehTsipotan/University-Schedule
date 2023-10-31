package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Course;
import com.university.schedule.model.Teacher;
import com.university.schedule.repository.TeacherRepository;
import com.university.schedule.validation.TeacherEntityValidator;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class DefaultTeacherServiceTest {

    private DefaultTeacherService defaultTeacherService;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private ConverterService converterService;

    @Mock
    private TeacherEntityValidator teacherEntityValidator;

    @BeforeEach
    public void setUp() {
        defaultTeacherService = new DefaultTeacherService(teacherRepository, converterService, teacherEntityValidator);
    }

    @Test
    public void findAll_success() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(teacherRepository.findAll()).thenReturn(List.of(teacher));

        assertEquals(List.of(teacher), defaultTeacherService.findAll());

        verify(teacherRepository).findAll();
    }

    @Test
    public void findAll_whenTeacherRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(teacherRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultTeacherService.findAll());

        verify(teacherRepository).findAll();
    }

    @Test
    public void findAll_whenNoTeachersFound_returnEmptyList() {
        when(teacherRepository.findAll()).thenReturn(List.of());

        assertEquals(0, defaultTeacherService.findAll().size());

        verify(teacherRepository).findAll();
    }

    @Test
    public void findAllAsDTO_noPageable_success() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        TeacherDTO teacherDTO = new TeacherDTO();
        teacher.setId(1L);

        when(teacherRepository.findAll()).thenReturn(List.of(teacher));
        when(converterService.convert(teacher, TeacherDTO.class)).thenReturn(teacherDTO);

        assertEquals(List.of(teacherDTO), defaultTeacherService.findAllAsDTO());

        verify(teacherRepository).findAll();
        verify(converterService).convert(teacher, TeacherDTO.class);
    }

    @Test
    public void findAllAsDTO_whenTeacherRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(teacherRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultTeacherService.findAllAsDTO());

        verify(teacherRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenNoTeachersFound_returnEmptyList() {
        when(teacherRepository.findAll()).thenReturn(List.of());

        assertEquals(0, defaultTeacherService.findAllAsDTO().size());

        verify(teacherRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenPageableIsValid_success() {
        Pageable pageable = mock(Pageable.class);

        Teacher teacher = new Teacher();
        teacher.setId(1L);

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(1L);

        when(teacherRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(teacher)));
        when(converterService.convert(teacher, TeacherDTO.class)).thenReturn(teacherDTO);

        assertEquals(List.of(teacherDTO), defaultTeacherService.findAllAsDTO(pageable));

        verify(teacherRepository).findAll(pageable);
        verify(converterService).convert(teacher, TeacherDTO.class);
    }

    @ParameterizedTest
    @NullSource
    public void findAllAsDTO_whenPageableIsNull_throwIllegalArgumentException(Pageable pageable) {
        assertThrows(IllegalArgumentException.class, () -> defaultTeacherService.findAllAsDTO(pageable));
    }

    @Test
    public void findAllAsDTO_whenWithValidPageableTeacherRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Pageable pageable = mock(Pageable.class);

        when(teacherRepository.findAll(pageable)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultTeacherService.findAllAsDTO(pageable));

        verify(teacherRepository).findAll(pageable);
    }

    @Test
    public void save_success() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(teacherRepository.save(teacher)).thenReturn(teacher);

        assertEquals(teacher.getId(), defaultTeacherService.save(teacher));

        verify(teacherRepository).save(teacher);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenTeacherIsNull_throwIllegalArgumentException(Teacher nullTeacher) {
        doThrow(IllegalArgumentException.class).when(teacherEntityValidator).validate(nullTeacher);
        assertThrows(IllegalArgumentException.class, () -> defaultTeacherService.save(nullTeacher));
        verify(teacherEntityValidator).validate(nullTeacher);
    }

    @Test
    public void save_whenTeacherIsInvalid_throwValidationException() {
        doThrow(ValidationException.class).when(teacherEntityValidator).validate(any(Teacher.class));

        assertThrows(ValidationException.class, () -> defaultTeacherService.save(new Teacher()));
        verify(teacherEntityValidator).validate(any(Teacher.class));
    }

    @Test
    public void save_whenTeacherRepositoryThrowsExceptionExtendsDataAccessException() {
        when(teacherRepository.save(any(Teacher.class))).thenThrow(InvalidDataAccessApiUsageException.class);

        assertThrows(ServiceException.class, () -> defaultTeacherService.save(new Teacher()));

        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    public void update_success() {
        Teacher foundedTeacher = new Teacher();
        foundedTeacher.setId(1L);
        foundedTeacher.setPassword("password");

        Teacher teacher = new Teacher();
        teacher.setId(1L);

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(1L);

        when(teacherRepository.findById(teacherDTO.getId())).thenReturn(Optional.of(teacher));
        when(converterService.convert(teacherDTO, Teacher.class)).thenReturn(teacher);
        when(teacherRepository.save(teacher)).thenReturn(teacher);

        assertEquals(1L, defaultTeacherService.update(teacherDTO));

        verify(teacherRepository).save(teacher);
        verify(teacherRepository).findById(teacherDTO.getId());
        verify(converterService).convert(teacherDTO, Teacher.class);
    }

    @ParameterizedTest
    @NullSource
    public void update_whenTeacherDTOIsNull_throwIllegalArgumentException(TeacherDTO nullTeacherDTO) {
        assertThrows(IllegalArgumentException.class, () -> defaultTeacherService.update(nullTeacherDTO));
    }

    @Test
    public void update_whenTeacherRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(1L);

        when(teacherRepository.findById(teacherDTO.getId())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultTeacherService.update(teacherDTO));

        verify(teacherRepository).findById(teacherDTO.getId());
    }

    @Test
    public void update_whenStudentIsInvalid_throwValidationException() {
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(1L);

        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(teacherRepository.findById(teacherDTO.getId())).thenReturn(Optional.of(teacher));
        when(converterService.convert(teacherDTO, Teacher.class)).thenReturn(teacher);
        doThrow(ValidationException.class).when(teacherEntityValidator).validate(teacher);

        assertThrows(ValidationException.class, () -> defaultTeacherService.update(teacherDTO));

        verify(teacherRepository).findById(teacherDTO.getId());
        verify(teacherEntityValidator).validate(teacher);
    }

    @Test
    public void findById_success() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(teacherRepository.findById(any(Long.class))).thenReturn(Optional.of(teacher));

        assertEquals(teacher, defaultTeacherService.findById(1L));

        verify(teacherRepository).findById(1L);
    }

    @ParameterizedTest
    @NullSource
    public void findById_whenIdIsNull_throwServiceException(Long nullId) {
        when(teacherRepository.findById(null)).thenThrow(InvalidDataAccessApiUsageException.class);

        assertThrows(ServiceException.class, () -> defaultTeacherService.findById(nullId));

        verify(teacherRepository).findById(nullId);
    }

    @Test
    public void findById_whenNoTeacherFounded_throwServiceException() {
        when(teacherRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> defaultTeacherService.findById(1L));

        verify(teacherRepository).findById(1L);
    }

    @Test
    public void findByIdAsDTO_success() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(1L);

        when(teacherRepository.findById(any(Long.class))).thenReturn(Optional.of(teacher));
        when(converterService.convert(teacher, TeacherDTO.class)).thenReturn(teacherDTO);

        assertEquals(teacherDTO, defaultTeacherService.findByIdAsDTO(1L));

        verify(teacherRepository).findById(1L);
        verify(converterService).convert(teacher, TeacherDTO.class);
    }

    @Test
    public void findByIdAsDTO_whenTeacherRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(teacherRepository.findById(teacher.getId())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultTeacherService.findByIdAsDTO(teacher.getId()));

        verify(teacherRepository).findById(teacher.getId());
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDTO_whenIdIsNull_throwServiceException(Long nullId) {
        when(teacherRepository.findById(null)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultTeacherService.findByIdAsDTO(nullId));
    }

    @Test
    public void findByIdAsDTO_whenStudentIsNotFound_throwServiceException() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> defaultTeacherService.findByIdAsDTO(teacher.getId()));

        verify(teacherRepository).findById(teacher.getId());
    }

    @Test
    public void deleteById_success() {
        when(teacherRepository.existsById(1L)).thenReturn(true);

        defaultTeacherService.deleteById(1L);

        verify(teacherRepository).existsById(1L);
        verify(teacherRepository).deleteById(1L);
    }

    @Test
    public void deleteById_whenTeacherRepositoryThrowsExceptionExtendsDataAccessException_thenServiceException() {
        when(teacherRepository.existsById(1L)).thenReturn(true);
        doThrow(BadJpqlGrammarException.class).when(teacherRepository).deleteById(1L);

        assertThrows(ServiceException.class, () -> defaultTeacherService.deleteById(1L));

        verify(teacherRepository).existsById(1L);
        verify(teacherRepository).deleteById(1L);
    }

    @Test
    public void deleteById_whenTeacherDoesNotExist_throwServiceException() {
        when(teacherRepository.existsById(1L)).thenReturn(false);

        assertThrows(ServiceException.class, () -> defaultTeacherService.deleteById(1L));

        verify(teacherRepository).existsById(1L);
    }

    @Test
    public void deleteById_whenIdIsNull_throwServiceException() {
        when(teacherRepository.existsById(null)).thenThrow(InvalidDataAccessApiUsageException.class);

        assertThrows(ServiceException.class, () -> defaultTeacherService.deleteById(null));

        verify(teacherRepository).existsById(null);
    }

    @ParameterizedTest
    @NullSource
    public void findByCourse_whenCourseIsNull_throwServiceException(Course nullCourse) {
        when(teacherRepository.findByCourses(null)).thenThrow(InvalidDataAccessApiUsageException.class);

        assertThrows(ServiceException.class, () -> defaultTeacherService.findByCourses(nullCourse));

        verify(teacherRepository).findByCourses(nullCourse);
    }

    @Test
    public void findByCourse_success() {
        Course course = new Course();
        course.setId(1L);

        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(teacherRepository.findByCourses(any(Course.class))).thenReturn(List.of(teacher));

        assertEquals(List.of(teacher), defaultTeacherService.findByCourses(course));

        verify(teacherRepository).findByCourses(course);
    }


}
