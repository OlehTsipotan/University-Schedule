package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.StudentDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Student;
import com.university.schedule.repository.StudentRepository;
import com.university.schedule.validation.StudentEntityValidator;
import com.university.schedule.visitor.UserPageableStudentVisitor;
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
public class DefaultStudentServiceTest {

    private DefaultStudentService defaultStudentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserService userService;

    @Mock
    private ConverterService converterService;

    @Mock
    private StudentEntityValidator studentEntityValidator;

    @Mock
    private UserPageableStudentVisitor userPageableStudentVisitor;

    @BeforeEach
    void setUp() {
        defaultStudentService =
            new DefaultStudentService(studentRepository, userService, converterService, studentEntityValidator,
                userPageableStudentVisitor);
    }

    @Test
    public void findAll_success() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findAll()).thenReturn(List.of(student));

        assertEquals(List.of(student), defaultStudentService.findAll());

        verify(studentRepository).findAll();
    }

    @Test
    public void findAll_whenStudentRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(studentRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultStudentService.findAll());

        verify(studentRepository).findAll();
    }

    @Test
    public void findAllAsDTO_success() {
        Student student = new Student();
        student.setId(1L);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(1L);

        when(converterService.convert(student, StudentDTO.class)).thenReturn(studentDTO);
        when(studentRepository.findAll()).thenReturn(List.of(student));

        assertEquals(List.of(studentDTO), defaultStudentService.findAllAsDTO());

        verify(studentRepository).findAll();
        verify(converterService).convert(student, StudentDTO.class);
    }

    @Test
    public void findAllAsDTO_whenStudentRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(studentRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultStudentService.findAllAsDTO());

        verify(studentRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenPageableIsValid_success() {
        Student student = new Student();
        student.setId(1L);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(1L);

        Pageable pageable = mock(Pageable.class);

        when(converterService.convert(student, StudentDTO.class)).thenReturn(studentDTO);
        when(studentRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(student)));

        assertEquals(List.of(studentDTO), defaultStudentService.findAllAsDTO(pageable));

        verify(studentRepository).findAll(pageable);
        verify(converterService).convert(student, StudentDTO.class);
    }

    @ParameterizedTest
    @NullSource
    public void findAllAsDTO_whenPageableIsNull_throwIllegalArgumentException(Pageable pageable) {
        assertThrows(IllegalArgumentException.class, () -> defaultStudentService.findAllAsDTO(pageable));
    }

    @Test
    public void findAllAsDTO_whenPageableIsValidAndStudentRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Pageable pageable = mock(Pageable.class);

        when(studentRepository.findAll(pageable)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultStudentService.findAllAsDTO(pageable));

        verify(studentRepository).findAll(pageable);
    }

    @Test
    public void save_success() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.save(student)).thenReturn(student);

        assertEquals(1L, defaultStudentService.save(student));

        verify(studentRepository).save(student);
    }

    @Test
    public void save_whenStudentRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.save(student)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultStudentService.save(student));

        verify(studentRepository).save(student);
    }

    @Test
    public void save_whenStudentIsInvalid_throwValidationException() {
        Student student = new Student();
        student.setId(1L);

        doThrow(ValidationException.class).when(studentEntityValidator).validate(student);

        assertThrows(ValidationException.class, () -> defaultStudentService.save(student));

        verify(studentEntityValidator).validate(student);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenStudentIsNull_throwIllegalArgument(Student nullStudent) {
        doThrow(IllegalArgumentException.class).when(studentEntityValidator).validate(nullStudent);
        assertThrows(ServiceException.class, () -> defaultStudentService.save(nullStudent));
    }

    @Test
    public void update_success() {
        Student foundedStudent = new Student();
        foundedStudent.setId(1L);
        foundedStudent.setPassword("password");

        Student student = new Student();
        student.setId(1L);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(1L);

        when(studentRepository.findById(studentDTO.getId())).thenReturn(Optional.of(student));
        when(converterService.convert(studentDTO, Student.class)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);

        assertEquals(1L, defaultStudentService.update(studentDTO));

        verify(studentRepository).save(student);
        verify(studentRepository).findById(studentDTO.getId());
        verify(converterService).convert(studentDTO, Student.class);
    }

    @Test
    public void update_whenStudentRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(1L);

        when(studentRepository.findById(studentDTO.getId())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultStudentService.update(studentDTO));

        verify(studentRepository).findById(studentDTO.getId());
    }

    @Test
    public void update_whenStudentIsInvalid_throwValidationException() {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(1L);

        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(studentDTO.getId())).thenReturn(Optional.of(student));
        when(converterService.convert(studentDTO, Student.class)).thenReturn(student);
        doThrow(ValidationException.class).when(studentEntityValidator).validate(student);

        assertThrows(ValidationException.class, () -> defaultStudentService.update(studentDTO));

        verify(studentRepository).findById(studentDTO.getId());
        verify(studentEntityValidator).validate(student);
    }

    @ParameterizedTest
    @NullSource
    public void update_whenStudentIsNull_throwIllegalArgumentException(StudentDTO nullStudentDTO) {
        assertThrows(IllegalArgumentException.class, () -> defaultStudentService.update(nullStudentDTO));
    }

    @Test
    public void findById_success() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

        assertEquals(student, defaultStudentService.findById(student.getId()));

        verify(studentRepository).findById(student.getId());
    }

    @ParameterizedTest
    @NullSource
    public void findById_whenIdIsNull_throwServiceException(Long nullId) {
        when(studentRepository.findById(null)).thenThrow(ServiceException.class);
        assertThrows(ServiceException.class, () -> defaultStudentService.findById(nullId));
    }

    @Test
    public void findById_whenStudentRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(student.getId())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultStudentService.findById(student.getId()));

        verify(studentRepository).findById(student.getId());
    }

    @Test
    public void findById_whenStudentIsNotFound_throwServiceException() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> defaultStudentService.findById(student.getId()));

        verify(studentRepository).findById(student.getId());
    }

    @Test
    public void findByIdAsDTO_whenStudentRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(student.getId())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultStudentService.findByIdAsDTO(student.getId()));

        verify(studentRepository).findById(student.getId());
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDTO_whenIdIsNull_throwServiceException(Long nullId) {
        when(studentRepository.findById(null)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultStudentService.findByIdAsDTO(nullId));
    }

    @Test
    public void findByIdAsDTO_whenStudentIsNotFound_throwServiceException() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> defaultStudentService.findByIdAsDTO(student.getId()));

        verify(studentRepository).findById(student.getId());
    }

    @Test
    public void deleteById_success() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.existsById(student.getId())).thenReturn(true);

        defaultStudentService.deleteById(student.getId());

        verify(studentRepository).existsById(student.getId());
        verify(studentRepository).deleteById(student.getId());
    }

    @Test
    public void deleteById_whenStudentDoNotExists_throwsDeleteonFailedException() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.existsById(student.getId())).thenReturn(false);

        assertThrows(DeletionFailedException.class, () -> defaultStudentService.deleteById(student.getId()));

        verify(studentRepository).existsById(student.getId());
    }

    @ParameterizedTest
    @NullSource
    public void deleteById_whenIdIsNull_throwServiceException(Long nullId) {
        when(studentRepository.existsById(null)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultStudentService.deleteById(nullId));
    }

    @Test
    public void deleteById_whenStudentRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.existsById(student.getId())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultStudentService.deleteById(student.getId()));

        verify(studentRepository).existsById(student.getId());
    }

    @Test
    public void findAllAsDTO_whenEmailAndPageableAreValid_success() {
        Student student = new Student();
        student.setId(1L);

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(1L);

        Pageable pageable = mock(Pageable.class);
        String email = "email";

        when(userService.findByEmail(email)).thenReturn(student);
        when(studentRepository.findByGroup(student.getGroup(), pageable)).thenReturn(new PageImpl<>(List.of(student)));
        when(converterService.convert(student, StudentDTO.class)).thenReturn(studentDTO);
        when(userPageableStudentVisitor.performActionForStudent(student, pageable)).thenReturn(List.of(student));

        assertEquals(List.of(studentDTO), defaultStudentService.findAllAsDTO(email, pageable));

        verify(userService).findByEmail(email);
        verify(converterService).convert(student, StudentDTO.class);
        verify(userPageableStudentVisitor).performActionForStudent(student, pageable);
    }

    @ParameterizedTest
    @NullSource
    public void findAllAsDTO_whenPageableIsNullAndEmailIsValid_throwIllegalArgumentException(Pageable nullPageable) {
        String email = "email";

        assertThrows(IllegalArgumentException.class, () -> defaultStudentService.findAllAsDTO(email, nullPageable));
    }

    @ParameterizedTest
    @NullSource
    public void findAllAsDTO_whenPageableIsValidAndEmailIsNull_throwIllegalArgumentException(String nullEmail) {
        Pageable pageable = mock(Pageable.class);

        assertThrows(IllegalArgumentException.class, () -> defaultStudentService.findAllAsDTO(nullEmail, pageable));
    }

}
