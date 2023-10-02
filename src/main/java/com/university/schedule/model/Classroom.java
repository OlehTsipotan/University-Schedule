package com.university.schedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Table(name = "classrooms", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "building_id"})})
public class Classroom {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "classroom_generator")
	@SequenceGenerator(name = "classroom_generator", sequenceName = "classrooms_seq", allocationSize = 1)
	@Column(name = "classroom_id")
	private Long id;

	@NonNull
	@NotBlank(message = "Classroom name must not be blank")
	@Size(max = 255)
	private String name;

	@ManyToOne
	@JoinColumn(name = "building_id")
	@NonNull
	@NotNull(message = "Classroom building must not be null")
	private Building building;

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
