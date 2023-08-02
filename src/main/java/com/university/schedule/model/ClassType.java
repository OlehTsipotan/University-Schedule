package com.university.schedule.model;

import jakarta.persistence.*;
import lombok.*;

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
}
