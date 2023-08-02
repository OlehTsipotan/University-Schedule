package com.university.schedule.model;

import com.university.schedule.converter.DurationConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "class_times")
public class ClassTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_time_generator")
    @SequenceGenerator(name = "class_time_generator", sequenceName = "class_times_seq", allocationSize = 1)
    @Column(name = "class_time_id")
    private Long id;

    @Column(name = "order_number")
    @NonNull
    private Integer orderNumber;

    @Column(name = "start_time")
    @NonNull
    private LocalTime startTime;

    @Column(name = "duration_minutes")
    @NonNull
    @Convert(converter = DurationConverter.class)
    private Duration duration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ClassTime classTime = (ClassTime) o;
        return getId() != null && Objects.equals(getId(), classTime.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "order = " + orderNumber + ", " +
                "startTime = " + startTime + ", " +
                "duration = " + duration + ")";
    }
}
