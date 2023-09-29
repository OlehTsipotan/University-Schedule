package com.university.schedule.formatter;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StringToBuildingDTOFormatter implements Formatter<BuildingDTO> {

	private final BuildingService buildingService;

	@Override
	public BuildingDTO parse(String text, Locale locale) throws ParseException {
		return buildingService.findByIdAsDTO(Long.parseLong(text));
	}

	@Override
	public String print(BuildingDTO object, Locale locale) {
		return null;
	}
}
