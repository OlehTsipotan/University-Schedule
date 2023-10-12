package com.university.schedule.model;

import com.university.schedule.visitor.UserPageableCourseVisitor;
import com.university.schedule.visitor.UserPageableStudentVisitor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
	@SequenceGenerator(name = "user_generator", sequenceName = "users_seq", allocationSize = 1)
	@Column(name = "user_id")
	private Long id;

	@NotBlank(message = "User email must not be blank")
	@Size(max = 255)
	private String email;

	@NotBlank(message = "User password must not be blank")
	@Size(max = 255)
	private String password;

	@Column(name = "first_name")
	@NotBlank(message = "User firstName must not be blank")
	@Size(max = 255)
	private String firstName;

	@Column(name = "last_name")
	@NotBlank(message = "User lastName must not be blank")
	@Size(max = 255)
	private String lastName;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@Column(name = "is_enable")
	@Getter(AccessLevel.NONE)
	private Boolean isEnable;

	public List<Course> accept(UserPageableCourseVisitor visitor, Pageable pageable){
		return visitor.performActionForUser(pageable);
	}

	public List<Student> accept(UserPageableStudentVisitor visitor, Pageable pageable){
		return visitor.performActionForUser(pageable);
	}

	public User(Long id, String email, String password, String firstName, String lastName, Role role) {
		this(id, email, password, firstName, lastName, true, role);
	}

	public User(Long id, String email, String password, String firstName, String lastName, Boolean isEnable,
	            Role role) {
		this(email, password, firstName, lastName, isEnable, role);
		this.id = id;
	}

	public User(String email, String password, String firstName, String lastName, Role role) {
		this(email, password, firstName, lastName, true, role);
	}

	public User(String email, String password, String firstName, String lastName, Boolean isEnable, Role role) {
		this(email, password, firstName, lastName);
		this.role = role;
		this.isEnable = isEnable;
	}

	public User(String email, String password, String firstName, String lastName) {
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isEnable = true;
	}

	public Boolean isEnable() {
		return this.isEnable;
	}

	public String getFullName() {
		return String.format("%s %s", firstName, lastName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		User user = (User) o;
		return getId() != null && Objects.equals(getId(), user.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + "id = " + id + ", " + "email = " + email + ", " + "firstName = " +
		       firstName + ", " + "lastName = " + lastName + ", " + "role = " + role + ", " + "isEnable = " + isEnable +
		       ")";
	}
}
