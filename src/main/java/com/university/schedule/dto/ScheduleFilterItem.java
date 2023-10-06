package com.university.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScheduleFilterItem {

	private String email;

	private Long teacherId;

	private List<Long> groupIdList;

	private LocalDate startDate;

	private LocalDate endDate;

	private Long classTypeId;
}