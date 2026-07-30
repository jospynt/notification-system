package org.example.repository;

import org.example.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
class UserRepositoryTest {


    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17");


    @DynamicPropertySource
    static void configureProperties(
            DynamicPropertyRegistry registry
    ) {

        registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                postgres::getUsername
        );

        registry.add(
                "spring.datasource.password",
                postgres::getPassword
        );
    }


    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }


    @Test
    void shouldSaveUser() {

        User user = new User(
                "John",
                "john@mail.com",
                25
        );


        User savedUser =
                userRepository.save(user);


        assertNotNull(savedUser.getId());

        assertAll(
                () -> assertEquals(
                        "John",
                        savedUser.getName()
                ),
                () -> assertEquals(
                        "john@mail.com",
                        savedUser.getEmail()
                ),
                () -> assertEquals(
                        25,
                        savedUser.getAge()
                )
        );
    }


    @Test
    void shouldFindUserById() {

        User user = new User(
                "John",
                "john@mail.com",
                25
        );


        User savedUser =
                userRepository.save(user);


        Optional<User> result =
                userRepository.findById(savedUser.getId());


        assertTrue(result.isPresent());


        User foundUser = result.get();


        assertAll(
                () -> assertEquals(
                        savedUser.getId(),
                        foundUser.getId()
                ),
                () -> assertEquals(
                        "John",
                        foundUser.getName()
                ),
                () -> assertEquals(
                        "john@mail.com",
                        foundUser.getEmail()
                ),
                () -> assertEquals(
                        25,
                        foundUser.getAge()
                )
        );
    }


    @Test
    void shouldReturnEmptyWhenUserNotFoundById() {

        Optional<User> result =
                userRepository.findById(999L);


        assertTrue(result.isEmpty());
    }


    @Test
    void shouldFindUserByEmail() {

        User user = new User(
                "John",
                "john@mail.com",
                25
        );


        userRepository.save(user);


        Optional<User> result =
                userRepository.findByEmail(
                        "john@mail.com"
                );


        assertTrue(result.isPresent());

        assertEquals(
                "john@mail.com",
                result.get().getEmail()
        );
    }


    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {

        Optional<User> result =
                userRepository.findByEmail(
                        "unknown@mail.com"
                );


        assertTrue(result.isEmpty());
    }


    @Test
    void shouldFindAllUsers() {

        User firstUser = new User(
                "John",
                "john@mail.com",
                25
        );

        User secondUser = new User(
                "Alex",
                "alex@mail.com",
                30
        );


        userRepository.save(firstUser);
        userRepository.save(secondUser);


        var users =
                userRepository.findAll();


        assertEquals(
                2,
                users.size()
        );


        assertTrue(
                users.stream()
                        .anyMatch(user ->
                                user.getEmail()
                                        .equals("john@mail.com"))
        );


        assertTrue(
                users.stream()
                        .anyMatch(user ->
                                user.getEmail()
                                        .equals("alex@mail.com"))
        );
    }


    @Test
    void shouldUpdateUser() {

        User user = new User(
                "John",
                "john@mail.com",
                25
        );


        User savedUser =
                userRepository.save(user);


        savedUser.setName(
                "Updated John"
        );

        savedUser.setEmail(
                "updated@mail.com"
        );

        savedUser.setAge(
                35
        );


        User updatedUser =
                userRepository.save(savedUser);


        assertAll(
                () -> assertEquals(
                        "Updated John",
                        updatedUser.getName()
                ),

                () -> assertEquals(
                        "updated@mail.com",
                        updatedUser.getEmail()
                ),

                () -> assertEquals(
                        35,
                        updatedUser.getAge()
                )
        );
    }


    @Test
    void shouldDeleteUser() {

        User user = new User(
                "John",
                "john@mail.com",
                25
        );


        User savedUser =
                userRepository.save(user);


        userRepository.delete(savedUser);


        Optional<User> result =
                userRepository.findById(
                        savedUser.getId()
                );


        assertTrue(result.isEmpty());
    }
}