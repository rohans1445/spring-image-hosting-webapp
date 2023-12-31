package com.example.imagehosting.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    @NotBlank(message = "User name must not be blank")
    @Size(min = 1, max = 20, message = "Username must be between 1 to 20 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    private String password;

    @Email
    @NotBlank(message = "Email must not be blank")
    private String email;
}
