package com.university.schedule.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "DayScheduleItem course must not be null")
    private Course course;

    @NonNull
    @NotNull(message = "DayScheduleItem teacher must not be null")
    private Teacher teacher;

    @NonNull
    @NotNull(message = "DayScheduleItem classroom must not be null")
    private Classroom classroom;

    @NonNull
    @NotNull(message = "DayScheduleItem classTime must not be null")
    private ClassTime classTime;

    @NonNull
    @NotNull(message = "DayScheduleItem dayOfWeek must not be null")
    private DayOfWeek dayOfWeek;

    @NonNull
    @NotNull(message = "DayScheduleItem classType must not be null")
    private ClassType classType;

    @NonNull
    @NotEmpty(message = "DayScheduleItem groups must not be empty")
    private Set<Group> groups = new HashSet<>();
}
