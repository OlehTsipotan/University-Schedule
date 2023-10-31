package com.university.schedule.converter;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;

public class GroupEntityToGroupDTOConverterTest {

    private GroupEntityToGroupDTOConverter groupEntityToGroupDTOConverter;

    @BeforeEach
    public void setUp() {
        groupEntityToGroupDTOConverter = new GroupEntityToGroupDTOConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenGroupIsNull_throwIllegalArgumentException(Group nullGroup) {
        assertThrows(IllegalArgumentException.class, () -> groupEntityToGroupDTOConverter.convert(nullGroup));
    }

    @Test
    public void convert_success() {
        Discipline discipline = new Discipline(1L, "discipline");
        Group group = new Group(1L, "group", discipline);

        GroupDTO groupDTO =
            GroupDTO.builder().id(1L).disciplineDTO(new DisciplineDTO(1L, "discipline")).name("group").build();

        assertEquals(groupDTO, groupEntityToGroupDTOConverter.convert(group));
    }

    @Test
    public void convert_whenGroupDTOFieldsAreNull_success() {
        Group group = new Group();

        assertDoesNotThrow(() -> groupEntityToGroupDTOConverter.convert(group));
    }
}
