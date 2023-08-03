package com.university.schedule.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "class_types")
@Entity
public class ClassType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_type_generator")
    @SequenceGenerator(name = "class_type_generator", sequenceName = "class_types_seq", allocationSize = 1)
    @Column(name = "class_type_id")
    private Long id;

    @NonNull
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ClassType classType = (ClassType) o;
        return getId() != null && Objects.equals(getId(), classType.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
