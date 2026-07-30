package org.example.controller;

import org.example.dto.UpdateUserRequest;
import tools.jackson.databind.ObjectMapper;
import org.example.dto.CreateUserRequest;
import org.example.entity.User;
import org.example.exception.UserNotFoundException;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;


    @Test
    void shouldFindUserById() throws Exception {

        User user = new User(
                "John",
                "john@example.com",
                30
        );

        ReflectionTestUtils.setField(user, "id", 1L);

        when(userService.findUserById(1L))
                .thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {

        when(userService.findUserById(1L))
                .thenThrow(new UserNotFoundException("User with id 1 not found."));


        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("User with id 1 not found."));
    }

    @Test
    void shouldFindAllUsers() throws Exception {

        User firstUser = new User(
                "John",
                "john@example.com",
                25
        );

        ReflectionTestUtils.setField(firstUser, "id", 1L);


        User secondUser = new User(
                "Alex",
                "alex@example.com",
                30
        );

        ReflectionTestUtils.setField(secondUser, "id", 2L);


        when(userService.findAll())
                .thenReturn(List.of(firstUser, secondUser));


        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Alex"))
                .andExpect(jsonPath("$[1].email").value("alex@example.com"));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersFound() throws Exception {

        when(userService.findAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldCreateUser() throws Exception {

        User user = new User(
                "John",
                "john@example.com",
                25
        );

        ReflectionTestUtils.setField(user, "id", 1L);


        when(userService.create(any(User.class)))
                .thenReturn(user);


        CreateUserRequest request = new CreateUserRequest(
                "John",
                "john@example.com",
                25
        );


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void shouldReturn400WhenCreateUserRequestIsInvalid() throws Exception {

        CreateUserRequest request = new CreateUserRequest(
                "",
                "invalid-email",
                200
        );

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(userService);
    }

    @Test
    void shouldUpdateUser() throws Exception {

        User updatedUser = new User(
                "Updated John",
                "updated@example.com",
                35
        );

        ReflectionTestUtils.setField(updatedUser, "id", 1L);


        when(userService.update(any(Long.class), any(User.class)))
                .thenReturn(updatedUser);


        UpdateUserRequest request = new UpdateUserRequest(
                "Updated John",
                "updated@example.com",
                35
        );


        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated John"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.age").value(35));
    }

    @Test
    void shouldReturn400WhenUpdateUserRequestIsInvalid() throws Exception {

        UpdateUserRequest request = new UpdateUserRequest(
                "",
                "invalid-email",
                200
        );

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(userService);
    }

    @Test
    void shouldDeleteUser() throws Exception {

        doNothing()
                .when(userService)
                .deleteById(1L);


        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeleteUserNotFound() throws Exception {

        doThrow(new UserNotFoundException("User with id 1 not found."))
                .when(userService)
                .deleteById(1L);


        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("User with id 1 not found."));
    }
}