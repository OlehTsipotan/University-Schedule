package com.university.schedule.converter;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GroupDTOToGroupEntityConverterTest {

    private GroupDTOToGroupEntityConverter groupDTOToGroupEntityConverter;

    @BeforeEach
    public void setUp() {
        groupDTOToGroupEntityConverter = new GroupDTOToGroupEntityConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenGroupDTOIsNull_throwIllegalArgumentException(GroupDTO nullGroupDTO) {
        assertThrows(IllegalArgumentException.class, () -> groupDTOToGroupEntityConverter.convert(nullGroupDTO));
    }

    @Test
    public void convert_whenGroupDTOFieldsAreNull_success() {
        GroupDTO groupDTO = GroupDTO.builder().build();
        Group group = new Group();

        Group groupConverted = groupDTOToGroupEntityConverter.convert(groupDTO);

        assertEquals(group.getId(), groupConverted.getId());
        assertEquals(group.getName(), groupConverted.getName());
        assertEquals(group.getCourses(), groupConverted.getCourses());
        assertEquals(group.getDiscipline(), groupConverted.getDiscipline());
    }

    @Test
    public void convert_whenGroupDTOCoursesAreEmptyList_success() {
        GroupDTO groupDTO = GroupDTO.builder().courseDTOS(new ArrayList<>()).build();
        Group group = new Group();

        Group groupConverted = groupDTOToGroupEntityConverter.convert(groupDTO);

        assertEquals(group.getId(), groupConverted.getId());
        assertEquals(group.getName(), groupConverted.getName());
        assertEquals(group.getCourses(), groupConverted.getCourses());
        assertEquals(group.getDiscipline(), groupConverted.getDiscipline());
    }

    @Test
    public void convert_success() {
        GroupDTO groupDTO =
            GroupDTO.builder().id(1L).name("group").disciplineDTO(new DisciplineDTO(1L, "discipline")).build();
        Group group = new Group(1L, "group", new Discipline(1L, "discipline"));

        Group groupConverted = groupDTOToGroupEntityConverter.convert(groupDTO);

        assertEquals(group.getId(), groupConverted.getId());
        assertEquals(group.getName(), groupConverted.getName());
        assertEquals(group.getCourses(), groupConverted.getCourses());
        assertEquals(group.getDiscipline(), groupConverted.getDiscipline());
    }
}
