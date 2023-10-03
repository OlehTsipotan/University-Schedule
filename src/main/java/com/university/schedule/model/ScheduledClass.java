package com.university.schedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "scheduled_classes")
public class ScheduledClass {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduled_class_generator")
	@SequenceGenerator(name = "scheduled_class_generator", sequenceName = "scheduled_classes_seq", allocationSize = 1)
	@Column(name = "scheduled_class_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "course_id")
	@NotNull(message = "ScheduledClass course must not be null")
	private Course course;

	@ManyToOne
	@JoinColumn(name = "teacher_id")
	@NotNull(message = "ScheduledClass teacher must not be null")
	private Teacher teacher;

	@ManyToOne
	@JoinColumn(name = "classroom_id")
	private Classroom classroom;

	@ManyToOne
	@JoinColumn(name = "class_time_id")
	@NotNull(message = "ScheduledClass classTime must not be null")
	private ClassTime classTime;

	@Column(name = "class_date")
	@NotNull(message = "ScheduledClass date must not be null")
	private LocalDate date;

	@ManyToOne
	@JoinColumn(name = "type_id")
	@NotNull(message = "ScheduledClass classType must not be null")
	private ClassType classType;

	@ManyToMany
	@JoinTable(name = "scheduled_classes_groups", joinColumns = @JoinColumn(name = "scheduled_class_id"),
	           inverseJoinColumns = @JoinColumn(name = "group_id"))
	@ToString.Exclude
	@NotNull(message = "ScheduledClass groups (Set) must not be null")
	private Set<Group> groups = new HashSet<>();

	public ScheduledClass(Long id, Course course, Teacher teacher, ClassTime classTime, LocalDate date,
	                      ClassType classType, Set<Group> groups) {
		this.id = id;
		this.course = course;
		this.teacher = teacher;
		this.classTime = classTime;
		this.date = date;
		this.classType = classType;
		this.groups = groups;
	}

	public ScheduledClass(Course course, Teacher teacher, ClassTime classTime, LocalDate date, ClassType classType,
	                      Set<Group> groups) {
		this.course = course;
		this.teacher = teacher;
		this.classTime = classTime;
		this.date = date;
		this.classType = classType;
		this.groups = groups;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		ScheduledClass that = (ScheduledClass) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
