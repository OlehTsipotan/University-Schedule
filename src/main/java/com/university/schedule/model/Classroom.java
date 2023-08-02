package com.university.schedule.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "classrooms")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "classroom_generator")
    @SequenceGenerator(name = "classroom_generator", sequenceName = "classrooms_seq", allocationSize = 1)
    @Column(name = "classroom_id")
    private Long id;

    @NonNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    public Classroom(@NonNull String name, Building building) {
        this.name = name;
        this.building = building;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Classroom classroom = (Classroom) o;
        return getId() != null && Objects.equals(getId(), classroom.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
