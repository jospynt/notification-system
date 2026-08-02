package org.example.event;

import org.example.dto.UserOperation;

public record UserEvent(
        UserOperation operation,
        String email
) {
}