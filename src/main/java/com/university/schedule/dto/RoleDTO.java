package com.university.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDTO {

    private Long id;

    @NotBlank(message = "RoleDTO name must not be blank")
    @Size(max = 255)
    private String name;

    private List<AuthorityDTO> authorityDTOS;

    public RoleDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
