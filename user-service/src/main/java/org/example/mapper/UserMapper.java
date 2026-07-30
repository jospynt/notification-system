package org.example.mapper;

import org.example.dto.CreateUserRequest;
import org.example.dto.UpdateUserRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }

    public static User toEntity(CreateUserRequest request) {
        return new User(
                request.name(),
                request.email(),
                request.age()
        );
    }

    public static User toEntity(UpdateUserRequest request) {
        return new User(
                request.name(),
                request.email(),
                request.age()
        );
    }
}