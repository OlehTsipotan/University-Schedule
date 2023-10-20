package com.university.schedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "authorities")
public class Authority {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_generator")
	@SequenceGenerator(name = "authority_generator", sequenceName = "authorities_seq", allocationSize = 1)
	@Column(name = "authority_id")
	private Long id;

	@NotBlank(message = "Authority name must not be blank")
	@Size(max = 255)
	private String name;

	@ManyToMany
	@JoinTable(name = "roles_authorities", joinColumns = @JoinColumn(name = "authority_id"),
	           inverseJoinColumns = @JoinColumn(name = "role_id"))
	@ToString.Exclude
	private Set<Role> roles;

	public Authority(String name){
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Authority authority = (Authority) o;
		return getId() != null && Objects.equals(getId(), authority.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
