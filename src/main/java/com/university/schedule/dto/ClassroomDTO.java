package com.university.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClassroomDTO {

    private Long id;

    @NonNull
    @NotBlank(message = "Classroom name must not be blank")
    @Size(max = 255)
    private String name;

    @NotNull(message = "Building must not be null")
    private BuildingDTO buildingDTO;

}
