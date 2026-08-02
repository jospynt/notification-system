package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {

        User user = userService.findUserById(id);

        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {

        List<UserResponse> users = userService.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody UserRequest request) {

        User user = UserMapper.toEntity(request);

        User createdUser = userService.create(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserMapper.toResponse(createdUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {

        User user = UserMapper.toEntity(request);

        User updatedUser = userService.update(id, user);

        return ResponseEntity.ok(UserMapper.toResponse(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        userService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}