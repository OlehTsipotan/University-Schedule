package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.CourseDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Course;
import com.university.schedule.model.Group;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import com.university.schedule.repository.CourseRepository;
import com.university.schedule.validation.CourseEntityValidator;
import com.university.schedule.visitor.UserPageableCourseVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.BadJpqlGrammarException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class DefaultCourseServiceTest {

    private DefaultCourseService defaultCourseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ConverterService converterService;

    @Mock
    private CourseEntityValidator courseEntityValidator;

    @Mock
    private UserPageableCourseVisitor userPageableCourseVisitor;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        defaultCourseService = new DefaultCourseService(courseRepository, converterService, courseEntityValidator,
            userPageableCourseVisitor, userService);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenCourseIsNull_throwIllegalArgumentException(Course nullCourse) {
        doThrow(IllegalArgumentException.class).when(courseEntityValidator).validate(nullCourse);
        assertThrows(IllegalArgumentException.class, () -> defaultCourseService.save(nullCourse));

        verify(courseEntityValidator).validate(nullCourse);
        verifyNoInteractions(courseRepository);
    }

    @Test
    public void save_whenCourseIsValid_success() {
        Course course = new Course();
        when(courseRepository.save(course)).thenReturn(course);
        assertDoesNotThrow(() -> defaultCourseService.save(course));

        verify(courseEntityValidator).validate(course);
        verify(courseRepository).save(course);
    }

    @Test
    public void save_whenCourseIsNotValid_throwValidationException() {
        Course course = new Course();
        doThrow(ServiceException.class).when(courseEntityValidator).validate(course);
        assertThrows(ServiceException.class, () -> defaultCourseService.save(course));

        verify(courseEntityValidator).validate(course);
        verifyNoInteractions(courseRepository);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenCourseDTOIsNull_throwIllegalArgumentException(CourseDTO nullCourseDTO) {
        when(converterService.convert(nullCourseDTO, Course.class)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> defaultCourseService.save(nullCourseDTO));

        verifyNoInteractions(courseRepository);
    }

    @Test
    public void save_whenCourseDTOIsValid_success() {
        CourseDTO courseDTO = CourseDTO.builder().id(1L).name("Course Name").build();
        Course course = new Course(1L, "Course Name");
        when(converterService.convert(courseDTO, Course.class)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        assertDoesNotThrow(() -> defaultCourseService.save(courseDTO));

        verify(converterService).convert(courseDTO, Course.class);
        verify(courseRepository).save(course);
    }

    @Test
    public void findByIdAsDTO_whenCourseIsNotFound_throwServiceException() {
        Long id = 1L;
        when(courseRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ServiceException.class, () -> defaultCourseService.findByIdAsDTO(id));

        verify(courseRepository).findById(id);
    }

    @Test
    public void findByIdAsDTO_whenCourseIsFound_success() {
        Long id = 1L;
        Course course = new Course(id, "Course Name");
        CourseDTO courseDTO = CourseDTO.builder().id(id).name("Course Name").build();

        when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        when(converterService.convert(course, CourseDTO.class)).thenReturn(courseDTO);

        CourseDTO resultDTO = defaultCourseService.findByIdAsDTO(id);

        assertEquals(courseDTO, resultDTO);

        verify(courseRepository).findById(id);
        verify(converterService).convert(course, CourseDTO.class);
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDTO_whenIdIsNull_throwServiceException(Long nullId) {
        when(courseRepository.findById(nullId)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultCourseService.findByIdAsDTO(nullId));
        verify(courseRepository).findById(nullId);
    }

    @Test
    public void findAll_success() {
        Course course = new Course();

        when(courseRepository.findAll()).thenReturn(List.of(course));

        assertEquals(List.of(course), defaultCourseService.findAll());
    }

    @Test
    public void findAll_whenCourseRepositoryThrowsExceptionExtendsDataAccessException_throwsServiceException() {
        when(courseRepository.findAll()).thenThrow(BadJpqlGrammarException.class);
        assertThrows(ServiceException.class, () -> defaultCourseService.findAll());
    }

    @Test
    public void findAllAsDTO_whenNoCoursesExist_returnEmptyList() {
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());
        List<CourseDTO> result = defaultCourseService.findAllAsDTO();
        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllAsDTO_whenCoursesExist_returnDTOList() {
        Course course = new Course(1L, "Course 1");
        CourseDTO courseDTO = CourseDTO.builder().id(1L).name("Course 1").build();
        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(converterService.convert(course, CourseDTO.class)).thenReturn(courseDTO);

        List<CourseDTO> result = defaultCourseService.findAllAsDTO();
        assertFalse(result.isEmpty());
        assertEquals(courseDTO, result.get(0));

        verify(courseRepository).findAll();
        verify(converterService).convert(course, CourseDTO.class);
    }

    @Test
    public void findAllAsDTO_whenPageableIsNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> defaultCourseService.findAllAsDTO("email", null));
        verifyNoInteractions(courseRepository);
    }

    @Test
    public void findAllAsDTO_whenUserIsNotFound_throwServiceException() {
        String email = "nonexistent@example.com";
        Pageable pageable = mock(Pageable.class);
        when(userService.findByEmail(email)).thenThrow(ServiceException.class);
        assertThrows(ServiceException.class, () -> defaultCourseService.findAllAsDTO(email, pageable));

        verify(userService).findByEmail(email);
        verifyNoInteractions(courseRepository);
    }

    @Test
    public void findAllAsDTO_whenPageableIsValid_success() {
        String email = "test@example.com";
        Pageable pageable = mock(Pageable.class);
        User user = mock(User.class);
        List<Course> courses = Collections.singletonList(new Course(1L, "Course 1"));
        List<CourseDTO> courseDTOs = Collections.singletonList(CourseDTO.builder().id(1L).name("Course 1").build());

        when(userService.findByEmail(email)).thenReturn(user);
        when(user.accept(userPageableCourseVisitor, pageable)).thenReturn(courses);
        when(converterService.convert(any(Course.class), eq(CourseDTO.class))).thenReturn(courseDTOs.get(0));

        List<CourseDTO> result = defaultCourseService.findAllAsDTO(email, pageable);
        assertFalse(result.isEmpty());
        assertEquals(courseDTOs, result);

        verify(userService).findByEmail(email);
        verify(user).accept(userPageableCourseVisitor, pageable);
        verify(converterService).convert(any(Course.class), eq(CourseDTO.class));
    }

    @Test
    public void findByTeacher_whenTeacherIsNull_throwIllegalArgumentException() {
        when(courseRepository.findByTeachers(null)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> defaultCourseService.findByTeacher(null));
        verify(courseRepository).findByTeachers(null);
    }

    @Test
    public void findByTeacher_success() {
        Course course = new Course();
        Teacher teacher = new Teacher();
        when(courseRepository.findByTeachers(teacher)).thenReturn(List.of(course));

        assertEquals(List.of(course), defaultCourseService.findByTeacher(teacher));
        verify(courseRepository).findByTeachers(teacher);
        verifyNoMoreInteractions(courseRepository);
    }

    @Test
    public void findByGroup_whenGroupIsNull_throwIllegalArgumentException() {
        when(courseRepository.findByGroups(null)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> defaultCourseService.findByGroup(null));
        verify(courseRepository).findByGroups(null);
    }

    @Test
    public void findByGroup_success() {
        Course course = new Course();
        Group group = new Group();
        when(courseRepository.findByGroups(group)).thenReturn(List.of(course));

        assertEquals(List.of(course), defaultCourseService.findByGroup(group));
        verify(courseRepository).findByGroups(group);
        verifyNoMoreInteractions(courseRepository);
    }

    @Test
    public void deleteById_whenCourseIsNotFound_throwDeletionFailedException() {
        Long id = 1L;
        when(courseRepository.existsById(id)).thenReturn(false);
        assertThrows(DeletionFailedException.class, () -> defaultCourseService.deleteById(id));

        verify(courseRepository).existsById(id);
        verifyNoMoreInteractions(courseRepository);
    }

    @Test
    public void deleteById_whenCourseIsFound_success() {
        Long id = 1L;
        when(courseRepository.existsById(id)).thenReturn(true);
        assertDoesNotThrow(() -> defaultCourseService.deleteById(id));

        verify(courseRepository).existsById(id);
        verify(courseRepository).deleteById(id);
    }

    @Test
    public void deleteById_whenCourseRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Long id = 1L;
        when(courseRepository.existsById(id)).thenReturn(true);
        doThrow(BadJpqlGrammarException.class).when(courseRepository).deleteById(id);

        assertThrows(ServiceException.class, () -> defaultCourseService.deleteById(id));

        verify(courseRepository).existsById(id);
        verify(courseRepository).deleteById(id);
    }

    @ParameterizedTest
    @NullSource
    public void findByGroupsName_whenGroupNameIsNull_throwServiceException(String nullName) {
        when(courseRepository.findByGroupsName(nullName)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultCourseService.findByGroupsName(nullName));
        verify(courseRepository).findByGroupsName(nullName);
    }

    @Test
    public void findByGroupsName_success() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course());
        String name = "name";
        when(courseRepository.findByGroupsName(any())).thenReturn(courses);
        assertEquals(courses, defaultCourseService.findByGroupsName(name));
        verify(courseRepository).findByGroupsName(name);
    }


}
