package com.university.schedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_generator")
	@SequenceGenerator(name = "role_generator", sequenceName = "roles_seq", allocationSize = 1)
	@Column(name = "role_id")
	private Long id;

	@NonNull
	@NotBlank(message = "Role name must not be blank")
	@Size(max = 255)
	private String name;

	@OneToMany(mappedBy = "role")
	@ToString.Exclude
	private List<User> users;

	@ManyToMany
	@JoinTable(name = "roles_authorities", joinColumns = @JoinColumn(name = "role_id"),
	           inverseJoinColumns = @JoinColumn(name = "authority_id"))
	@ToString.Exclude
	private Set<Authority> authorities;

	public Role(@NonNull Long id, @NonNull String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Role role = (Role) o;
		return getId() != null && Objects.equals(getId(), role.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
