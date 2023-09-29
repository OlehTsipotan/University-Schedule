package com.university.schedule.formatter;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StringToGroupDTOFormatter implements Formatter<GroupDTO> {

	private final GroupService groupService;

	@Override
	public GroupDTO parse(String text, Locale locale) throws ParseException {
		return groupService.findByIdAsDTO(Long.parseLong(text));
	}

	@Override
	public String print(GroupDTO object, Locale locale) {
		return null;
	}
}
