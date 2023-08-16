package com.university.schedule.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledClassDTO {

    private Long id;
    private String courseName;
    private String teacherFullName;
    private List<String> groupNames;
    private String date;
    private Integer classTimeOrderNumber;
    private String classTypeName;
    private String classroomName;
    private String classroomBuildingName;

}

