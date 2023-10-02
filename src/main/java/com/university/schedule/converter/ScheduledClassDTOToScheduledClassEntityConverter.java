package com.university.schedule.converter;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.model.Group;
import com.university.schedule.model.ScheduledClass;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScheduledClassDTOToScheduledClassEntityConverter implements Converter<ScheduledClassDTO, ScheduledClass> {

	private final ModelMapper modelMapper;
	private final GroupDTOToGroupEntityConverter groupDTOToGroupEntityConverter;


	public ScheduledClassDTOToScheduledClassEntityConverter() {
		this.modelMapper = new ModelMapper();
		this.groupDTOToGroupEntityConverter = new GroupDTOToGroupEntityConverter();

		org.modelmapper.Converter<List<GroupDTO>, Set<Group>> coursesListConverter =
				groupDTOList -> groupDTOList.getSource().stream().map(groupDTOToGroupEntityConverter::convert)
						.collect(Collectors.toSet());

		modelMapper.typeMap(ScheduledClassDTO.class, ScheduledClass.class).addMappings(modelMapper -> {
			modelMapper.using(coursesListConverter).map(ScheduledClassDTO::getGroupDTOS, ScheduledClass::setGroups);
		});
	}

	@Override
	public ScheduledClass convert(ScheduledClassDTO source) {
		return modelMapper.map(source, ScheduledClass.class);
	}
}
