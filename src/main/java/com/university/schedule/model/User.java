package com.university.schedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "users")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
	@SequenceGenerator(name = "user_generator", sequenceName = "users_seq", allocationSize = 1)
	@Column(name = "user_id")
	private Long id;

	@NonNull
	@NotBlank(message = "User email must not be blank")
	@Size(max = 255)
	private String email;

	@NonNull
	@NotBlank(message = "User password must not be blank")
	@Size(max = 255)
	private String password;

	@NonNull
	@Column(name = "first_name")
	@NotBlank(message = "User firstName must not be blank")
	@Size(max = 255)
	private String firstName;

	@NonNull
	@Column(name = "last_name")
	@NotBlank(message = "User lastName must not be blank")
	@Size(max = 255)
	private String lastName;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@NonNull
	@Column(name = "is_enable")
	@Getter(AccessLevel.NONE)
	private Boolean isEnable;

	public User(Long id, @NonNull String email, @NonNull String password, @NonNull String firstName,
	            @NonNull String lastName, @NonNull Role role) {
		this(id, email, password, firstName, lastName, true, role);
	}

	public User(@NonNull String email, @NonNull String password, @NonNull String firstName, @NonNull String lastName) {
		this(email, password, firstName, lastName, true);
	}

	public User(Long id, @NonNull String email, @NonNull String password, @NonNull String firstName,
	            @NonNull String lastName, @NonNull Boolean isEnable, @NonNull Role role) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isEnable = isEnable;
		this.role = role;
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
