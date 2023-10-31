package com.university.schedule.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
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
