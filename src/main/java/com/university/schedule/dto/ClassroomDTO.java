package com.university.schedule.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassroomDTO {

    private Long id;

    private String name;

    private String buildingName;
}
