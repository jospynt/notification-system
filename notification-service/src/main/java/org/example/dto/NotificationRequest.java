package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NotificationRequest (

    @Email(message = "Invalid email")
    @NotBlank(message = "Email must not be blank")
    String email,

    @NotBlank(message = "Subject must not be blank")
    String subject,

    @NotBlank(message = "Text must not be blank")
    String text

) {}
