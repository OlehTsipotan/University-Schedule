package com.university.schedule.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "scheduled_classes")
public class ScheduledClass {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduled_class_generator")
    @SequenceGenerator(name = "scheduled_class_generator", sequenceName = "scheduled_classes_seq", allocationSize = 1)
    @Column(name = "scheduled_class_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @NonNull
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @NonNull
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToOne
    @JoinColumn(name = "class_time_id")
    @NonNull
    private ClassTime classTime;

    @Column(name = "class_date")
    @NonNull
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private ClassType classType;

    @ManyToMany
    @JoinTable(name = "scheduled_classes_groups",
            joinColumns = @JoinColumn(name = "scheduled_class_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @ToString.Exclude
    private Set<Group> groups = new HashSet<>();


}
