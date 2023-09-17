package com.university.schedule.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassTimeDTO {

	private Long id;

	private Integer orderNumber;

	private LocalTime startTime;

	private Integer durationMinutes;
}
