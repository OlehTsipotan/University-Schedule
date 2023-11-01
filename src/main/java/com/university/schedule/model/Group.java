package com.university.schedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_generator")
    @SequenceGenerator(name = "group_generator", sequenceName = "groups_seq", allocationSize = 1)
    @Column(name = "group_id")
    private Long id;

    @NotBlank(message = "Group name must not be blank")
    @Size(max = 255)
    private String name;

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    @NotNull(message = "Group discipline must not be null")
    private Discipline discipline;

    @OneToMany(mappedBy = "group")
    @ToString.Exclude
    private List<Student> students = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "groups_courses", joinColumns = @JoinColumn(name = "group_id"),
               inverseJoinColumns = @JoinColumn(name = "course_id"))
    @ToString.Exclude
    private Set<Course> courses = new HashSet<>();

    public Group(Long id, String name, Discipline discipline) {
        this.id = id;
        this.name = name;
        this.discipline = discipline;
    }

    public Group(String name, Discipline discipline) {
        this.name = name;
        this.discipline = discipline;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Group group = (Group) o;
        return getId() != null && Objects.equals(getId(), group.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
