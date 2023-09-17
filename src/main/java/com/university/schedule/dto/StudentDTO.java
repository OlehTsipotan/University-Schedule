package com.university.schedule.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO extends UserDTO {

	private GroupDTO groupDTO;
}
