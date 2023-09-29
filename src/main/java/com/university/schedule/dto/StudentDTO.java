package com.university.schedule.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO extends UserDTO {

	private GroupDTO groupDTO;

	public StudentDTO(Long id, String email, String firstName, String lastName, RoleDTO roleDTO, Boolean isEnable,
	                  GroupDTO groupDTO) {
		super(id, email, firstName, lastName, roleDTO, isEnable);
		this.groupDTO = groupDTO;
	}
}
