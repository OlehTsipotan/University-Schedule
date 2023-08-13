package com.university.schedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;


import java.util.List;
import java.util.Objects;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "building_generator")
    @SequenceGenerator(name = "building_generator", sequenceName = "buildings_seq", allocationSize = 1)
    @Column(name = "building_id")
    private Long id;

    @NonNull
    @NotBlank(message = "Building name must not be blank")
    private String name;

    @NonNull
    @NotBlank(message = "Building address must not be blank")
    private String address;

    @OneToMany(mappedBy = "building")
    @ToString.Exclude
    private List<Classroom> classrooms;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Building building = (Building) o;
        return getId() != null && Objects.equals(getId(), building.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
