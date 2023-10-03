package com.university.schedule.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "courses")
@AllArgsConstructor
@NoArgsConstructor
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_generator")
	@SequenceGenerator(name = "course_generator", sequenceName = "courses_seq", allocationSize = 1)
	@Column(name = "course_id")
	private Long id;

	@NotBlank(message = "Course name must not be blank")
	@Size(max = 255)
	private String name;

	@ManyToMany(mappedBy = "courses")
	@ToString.Exclude
	private Set<Teacher> teachers = new HashSet<>();

	@ManyToMany(mappedBy = "courses")
	@ToString.Exclude
	private Set<Group> groups = new HashSet<>();

	public Course(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Course(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Course course = (Course) o;
		return getId() != null && Objects.equals(getId(), course.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
