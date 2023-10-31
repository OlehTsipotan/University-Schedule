package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Course;
import com.university.schedule.model.Teacher;
import com.university.schedule.repository.TeacherRepository;
import com.university.schedule.validation.TeacherEntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DefaultTeacherService implements TeacherService {

    private final TeacherRepository teacherRepository;

    private final ConverterService converterService;

    private final TeacherEntityValidator teacherEntityValidator;

    @Override
    public List<Teacher> findAll() {
        List<Teacher> teachers = execute(() -> teacherRepository.findAll());
        log.debug("Retrieved All {} Teachers", teachers.size());
        return teachers;
    }

    @Override
    public List<TeacherDTO> findAllAsDTO() {
        List<TeacherDTO> teacherDTOList =
            execute(() -> teacherRepository.findAll()).stream().map(this::convertToDTO).toList();
        log.debug("Retrieved All {} Teachers", teacherDTOList.size());
        return teacherDTOList;
    }

    @Override
    public List<TeacherDTO> findAllAsDTO(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable is null");
        }
        List<TeacherDTO> teacherDTOList =
            execute(() -> teacherRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
        log.debug("Retrieved All {} Teachers", teacherDTOList.size());
        return teacherDTOList;
    }

    @Override
    @Transactional
    public Long save(Teacher teacher) {
        execute(() -> {
            teacherEntityValidator.validate(teacher);
            teacherRepository.save(teacher);
        });
        log.info("saved {}", teacher);
        return teacher.getId();
    }

    @Override
    @Transactional
    public Long update(TeacherDTO teacherDTO) {
        if (teacherDTO == null) {
            throw new IllegalArgumentException("TeacherDTO is null");
        }
        Teacher teacherToSave = convertToExistingEntity(teacherDTO);
        execute(() -> {
            teacherEntityValidator.validate(teacherToSave);
            teacherRepository.save(teacherToSave);
        });
        log.info("saved {}", teacherToSave);
        return teacherToSave.getId();
    }

    public Teacher findById(Long id) {
        Teacher teacher =
            execute(() -> teacherRepository.findById(id)).orElseThrow(() -> new ServiceException("Teacher not found"));
        log.debug("Retrieved {}", teacher);
        return teacher;
    }

    @Override
    public TeacherDTO findByIdAsDTO(Long id) {
        Teacher teacher =
            execute(() -> teacherRepository.findById(id)).orElseThrow(() -> new ServiceException("Teacher not found"));
        log.debug("Retrieved {}", teacher);
        return convertToDTO(teacher);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        execute(() -> {
            if (!teacherRepository.existsById(id)) {
                throw new DeletionFailedException("There is no Teacher to delete with id = " + id);
            }
            teacherRepository.deleteById(id);
        });
        log.info("Deleted id = {}", id);
    }

    @Override
    public List<Teacher> findByCourses(Course course) {
        List<Teacher> teachers = execute(() -> teacherRepository.findByCourses(course));
        log.debug("Retrieved All {} Groups", teachers.size());
        return teachers;
    }

    private TeacherDTO convertToDTO(Teacher source) {
        return converterService.convert(source, TeacherDTO.class);
    }

    private Teacher convertToEntity(TeacherDTO source) {
        return converterService.convert(source, Teacher.class);
    }

    private Teacher convertToExistingEntity(TeacherDTO teacherDTO) {
        Teacher foundedTeacher = findById(teacherDTO.getId());
        Teacher teacherResult = convertToEntity(teacherDTO);
        teacherResult.setPassword(foundedTeacher.getPassword());
        return teacherResult;
    }

    private <T> T execute(DaoSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (DataAccessException e) {
            throw new ServiceException("DAO operation failed", e);
        }
    }

    private void execute(DaoProcessor processor) {
        try {
            processor.process();
        } catch (DataAccessException e) {
            throw new ServiceException("DAO operation failed", e);
        }
    }

    @FunctionalInterface
    public interface DaoSupplier<T> {
        T get();
    }

    @FunctionalInterface
    public interface DaoProcessor {
        void process();
    }
}
