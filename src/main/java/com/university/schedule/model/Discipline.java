package com.university.schedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "disciplines")
public class Discipline {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "discipline_generator")
    @SequenceGenerator(name = "discipline_generator", sequenceName = "disciplines_seq", allocationSize = 1)
    @Column(name = "discipline_id")
    private Long id;

    @NonNull
    @NotBlank(message = "Discipline name must not be blank")
    private String name;

    @OneToMany(mappedBy = "discipline")
    @ToString.Exclude
    private List<Group> groups;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Discipline that = (Discipline) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
