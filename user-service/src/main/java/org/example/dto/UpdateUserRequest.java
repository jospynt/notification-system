package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest (

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    @Min(value = 0, message = "Age must be at least 0")
    @Max(value = 150, message = "Age must not exceed 150")
    int age
) {}