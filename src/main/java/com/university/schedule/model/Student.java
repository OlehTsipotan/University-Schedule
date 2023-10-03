package com.university.schedule.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "student_id")
@NoArgsConstructor
public class Student extends User {

	@ManyToOne
	@JoinColumn(name = "group_id")
	private Group group;

	public Student(User user) {
		super(user.getId(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
				user.isEnable(), user.getRole());
	}

	public Student(String email, String password, String firstName, String lastName, Role role){
		super(email, password, firstName, lastName, role);
	}

	public Student(String email, String password, String firstName, String lastName){
		super(email, password, firstName, lastName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Student student = (Student) o;
		return getId() != null && Objects.equals(getId(), student.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + "id = " + getId() + ", " + "group = " + getGroup() + ", " +
		       "email = " + getEmail() + ", " + "firstName = " + getFirstName() + ", " + "lastName = " + getLastName() +
		       ", " + "role = " + getRole() + ", " + "isEnable = " + isEnable() + ")";
	}
}
