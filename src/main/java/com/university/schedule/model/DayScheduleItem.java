package com.university.schedule.model;

import lombok.*;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

// Not entity
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Builder
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class DayScheduleItem {

    @NonNull
    private Course course;

    @NonNull
    private Teacher teacher;

    @NonNull
    private Classroom classroom;

    @NonNull
    private ClassTime classTime;

    @NonNull
    private DayOfWeek dayOfWeek;

    @NonNull
    private ClassType classType;

    @NonNull
    private Set<Group> groups = new HashSet<>();
}
