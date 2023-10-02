package com.university.schedule.converter;

import com.university.schedule.dto.*;
import com.university.schedule.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public class ScheduledClassEntityToScheduledClassDTOConverter implements Converter<ScheduledClass, ScheduledClassDTO> {

	private final ModelMapper modelMapper;
	private final GroupEntityToGroupDTOConverter groupEntityToGroupDTOConverter;
	private final CourseEntityToCourseDTOConverter courseEntityToCourseDTOConverter;
	private final TeacherEntityToTeacherDTOConverter teacherEntityToTeacherDTOConverter;
	private final ClassroomEntityToClassroomDTOConverter classroomEntityToClassroomDTOConverter;
	private final ClassTimeEntityToClassTimeDTOConverter classTimeEntityToClassTimeDTOConverter;
	private final ClassTypeEntityToClassTypeDTOConverter classTypeEntityToClassTypeDTOConverter;

	public ScheduledClassEntityToScheduledClassDTOConverter() {
		this.modelMapper = new ModelMapper();
		this.groupEntityToGroupDTOConverter = new GroupEntityToGroupDTOConverter();
		this.courseEntityToCourseDTOConverter = new CourseEntityToCourseDTOConverter();
		this.teacherEntityToTeacherDTOConverter = new TeacherEntityToTeacherDTOConverter();
		this.classroomEntityToClassroomDTOConverter = new ClassroomEntityToClassroomDTOConverter();
		this.classTimeEntityToClassTimeDTOConverter = new ClassTimeEntityToClassTimeDTOConverter();
		this.classTypeEntityToClassTypeDTOConverter = new ClassTypeEntityToClassTypeDTOConverter();

		org.modelmapper.Converter<Set<Group>, List<GroupDTO>> groupListConverter =
				setGroup -> setGroup.getSource().stream().map(groupEntityToGroupDTOConverter::convert).toList();
		org.modelmapper.Converter<Course, CourseDTO> courseDTOConverter =
				course -> courseEntityToCourseDTOConverter.convert(course.getSource());
		org.modelmapper.Converter<Teacher, TeacherDTO> teacherDTOConverter =
				teacher -> teacherEntityToTeacherDTOConverter.convert(teacher.getSource());
		org.modelmapper.Converter<Classroom, ClassroomDTO> classroomDTOConverter =
				classroom -> classroomEntityToClassroomDTOConverter.convert(classroom.getSource());
		org.modelmapper.Converter<ClassTime, ClassTimeDTO> classTimeDTOConverter =
				classTime -> classTimeEntityToClassTimeDTOConverter.convert(classTime.getSource());
		org.modelmapper.Converter<ClassType, ClassTypeDTO> classTypeDTOConverter =
				classType -> classTypeEntityToClassTypeDTOConverter.convert(classType.getSource());


		modelMapper.typeMap(ScheduledClass.class, ScheduledClassDTO.class).addMappings(modelMapper -> {
			modelMapper.using(groupListConverter).map(ScheduledClass::getGroups, ScheduledClassDTO::setGroupDTOS);
			modelMapper.using(courseDTOConverter).map(ScheduledClass::getCourse, ScheduledClassDTO::setCourseDTO);
			modelMapper.using(teacherDTOConverter).map(ScheduledClass::getTeacher, ScheduledClassDTO::setTeacherDTO);
			modelMapper.using(classroomDTOConverter)
					.map(ScheduledClass::getClassroom, ScheduledClassDTO::setClassroomDTO);
			modelMapper.using(classTimeDTOConverter)
					.map(ScheduledClass::getClassTime, ScheduledClassDTO::setClassTimeDTO);
			modelMapper.using(classTypeDTOConverter)
					.map(ScheduledClass::getClassType, ScheduledClassDTO::setClassTypeDTO);
		});
	}

	@Override
	public ScheduledClassDTO convert(ScheduledClass source) {
		return modelMapper.map(source, ScheduledClassDTO.class);
	}
}

