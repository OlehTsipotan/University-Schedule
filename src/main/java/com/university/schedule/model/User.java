package com.university.schedule.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Setter
@Getter
@ToString
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
    private String email;

    @NonNull
    @NotBlank(message = "User password must not be blank")
    @Size(min = 4, max = 24)
    private String password;

    @NonNull
    @Column(name = "first_name")
    @NotBlank(message = "User firstName must not be blank")
    private String firstName;

    @NonNull
    @Column(name = "last_name")
    @NotBlank(message = "User lastName must not be blank")
    private String lastName;

    public String getFullName(){
        return String.format("%s %s", firstName, lastName);
    }
}
