package com.university.schedule.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private String name;

    @NonNull
    private String address;

    @OneToMany(mappedBy = "building")
    @ToString.Exclude
    private List<Classroom> classrooms;
}
