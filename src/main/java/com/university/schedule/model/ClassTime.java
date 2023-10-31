package com.university.schedule.model;

import com.university.schedule.converter.DurationAttributeConverter;
import com.university.schedule.validation.ClassDuration;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "class_times")
public class ClassTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_time_generator")
    @SequenceGenerator(name = "class_time_generator", sequenceName = "class_times_seq", allocationSize = 1)
    @Column(name = "class_time_id")
    private Long id;

    @Column(name = "order_number")
    @Positive(message = "ClassTime orderNumber must be greater than zero")
    @NotNull(message = "ClassTime orderNumber must not be null")
    private Integer orderNumber;

    @Column(name = "start_time")
    @NotNull(message = "ClassTime startTime must not be null")
    private LocalTime startTime;

    @Column(name = "duration_minutes")
    @NotNull(message = "ClassTime duration must not be null")
    @ClassDuration
    @Convert(converter = DurationAttributeConverter.class)
    private Duration duration;

    public ClassTime(Integer orderNumber, LocalTime startTime, Duration duration) {
        this.orderNumber = orderNumber;
        this.startTime = startTime;
        this.duration = duration;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + "id = " + id + ", " + "order = " + orderNumber + ", " +
            "startTime = " + startTime + ", " + "duration = " + duration + ")";
    }

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
}
