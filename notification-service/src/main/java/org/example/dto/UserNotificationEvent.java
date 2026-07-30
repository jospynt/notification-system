package org.example.dto;

public record UserNotificationEvent(
   UserOperation operation,
   String email
) {}
