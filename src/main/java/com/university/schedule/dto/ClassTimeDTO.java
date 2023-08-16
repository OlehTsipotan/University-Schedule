package com.university.schedule.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassTimeDTO {

    private Long id;

    private Integer orderNumber;

    private String startTime;

    private Integer durationMinutes;
}
