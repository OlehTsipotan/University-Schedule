package com.university.schedule.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

	@Positive(message = "ClassTime orderNumber must be greater than zero")
	@NotNull(message = "ClassTime orderNumber must not be null")
	private Integer orderNumber;

	@NotNull(message = "ClassTime startTime must not be null")
	private LocalTime startTime;

	@NotNull(message = "ClassTime duration must not be null")
	@Positive(message = "ClassTime durationMinutes must be greater than zero")
	private Integer durationMinutes;
}
