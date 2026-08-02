package org.example.service;

import org.example.entity.User;
import org.example.exception.UserNotFoundException;
import org.example.exception.ValidationException;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private static final Long ID = 1L;

    private static final String NAME = "Paul";
    private static final String EMAIL = "paul@mail.com";
    private static final int AGE = 30;

    @Mock
    private UserRepository userRepository;

    // NEW
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    class Create {
        @Test
        void shouldCreateUser() {

            User user = new User(NAME, EMAIL, AGE);

            when(userRepository.findByEmail(EMAIL))
                    .thenReturn(Optional.empty());

            when(userRepository.save(user))
                    .thenReturn(user);

            User result = userService.create(user);

            assertSame(user, result);

            verify(userRepository)
                    .findByEmail(EMAIL);

            verify(userRepository)
                    .save(user);

            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void shouldThrowExceptionWhenEmailAlreadyExists() {

            User existingUser = new User("Ivan", EMAIL, 25);


            User user = new User(NAME, EMAIL, AGE);


            when(userRepository.findByEmail(EMAIL))
                    .thenReturn(Optional.of(existingUser));


            ValidationException exception =
                    assertThrows(
                            ValidationException.class,
                            () -> userService.create(user)
                    );


            assertEquals(
                    "Email is already in use",
                    exception.getMessage()
            );


            verify(userRepository)
                    .findByEmail(EMAIL);

            verify(userRepository, never())
                    .save(any());
        }
    }

    @Nested
    class FindUserById {
        @Test
        void shouldFindUserById() {
            User user = new User(NAME, EMAIL, AGE);

            when(userRepository.findById(ID))
                    .thenReturn(Optional.of(user));

            User result = userService.findUserById(ID);

            assertSame(user, result);

            verify(userRepository).findById(ID);

            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(ID))
                    .thenReturn(Optional.empty());

            UserNotFoundException exception =
                    assertThrows(
                            UserNotFoundException.class,
                            () -> userService.findUserById(ID)
                    );


            assertEquals(
                    "User with id " + ID + " not found.",
                    exception.getMessage()
            );


            verify(userRepository)
                    .findById(ID);
        }
    }

    @Nested
    class FindAll {
        @Test
        void shouldReturnAllUsers() {
            List<User> users = List.of(
                    new User(NAME, EMAIL, AGE),
                    new User("Ivan", "ivan@mail.com", 25)
            );

            when(userRepository.findAll())
                    .thenReturn(users);

            List<User> result = userService.findAll();

            assertSame(users, result);

            verify(userRepository).findAll();
        }

        @Test
        void shouldReturnEmptyList() {
            when(userRepository.findAll())
                    .thenReturn(List.of());

            List<User> result = userService.findAll();

            assertTrue(result.isEmpty());

            verify(userRepository).findAll();
        }
    }

    @Nested
    class Update {
        @Test
        void shouldUpdateUser() {

            User existingUser = new User(NAME, EMAIL, AGE);

            ReflectionTestUtils.setField(
                    existingUser,
                    "id",
                    ID
            );

            User updatedUser = new User ("Updated", "updated@mail.com", 40);

            when(userRepository.findById(ID))
                    .thenReturn(Optional.of(existingUser));

            when(userRepository.findByEmail("updated@mail.com"))
                    .thenReturn(Optional.empty());

            when(userRepository.save(existingUser))
                    .thenReturn(existingUser);

            User result = userService.update(
                    ID,
                    updatedUser
            );

            assertAll(
                    () -> assertEquals("Updated", result.getName()),
                    () -> assertEquals("updated@mail.com", result.getEmail()),
                    () -> assertEquals(40, result.getAge())
            );

            verify(userRepository).findById(ID);
            verify(userRepository).findByEmail("updated@mail.com");
            verify(userRepository).save(existingUser);
        }

        @Test
        void shouldThrowExceptionWhenUserNotFound() {

            User user = new User(NAME, EMAIL, AGE);

            when(userRepository.findById(ID))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.update(ID, user)
            );

            verify(userRepository).findById(ID);
            verify(userRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            User user = new User(NAME, EMAIL, AGE);

            ReflectionTestUtils.setField(
                    user,
                    "id",
                    ID
            );


            User anotherUser = new User("Igor", EMAIL, 25);

            ReflectionTestUtils.setField(
                    anotherUser,
                    "id",
                    2L
            );

            User updatedUser = new User("Updated", EMAIL, 40);

            when(userRepository.findById(ID))
                    .thenReturn(Optional.of(user));
            when(userRepository.findByEmail(EMAIL))
                    .thenReturn(Optional.of(anotherUser));

            ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> userService.update(ID, updatedUser)
            );

            assertEquals(
                    "Email is already in use.",
                    exception.getMessage()
            );

            verify(userRepository).findById(ID);
            verify(userRepository).findByEmail(EMAIL);
            verify(userRepository, never()).save(any());
        }

        @Test
        void shouldAllowKeepingSameEmail() {

            User existingUser = new User(NAME, EMAIL, AGE);

            ReflectionTestUtils.setField(
                    existingUser,
                    "id",
                    ID
            );

            User updatedUser = new User("Updated", EMAIL, 40);

            when(userRepository.findById(ID))
                    .thenReturn(Optional.of(existingUser));
            when(userRepository.findByEmail(EMAIL))
                    .thenReturn(Optional.of(existingUser));
            when(userRepository.save(existingUser))
                    .thenReturn(existingUser);

            User result = userService.update(
                    ID,
                    updatedUser
            );

            assertAll(
                    () -> assertEquals("Updated", result.getName()),
                    () -> assertEquals(EMAIL, result.getEmail()),
                    () -> assertEquals(40, result.getAge())
            );

            verify(userRepository).save(existingUser);
        }
    }

    @Nested
    class DeleteById {
        @Test
        void shouldDeleteUserById() {

            User user = new User(NAME, EMAIL, AGE);

            when(userRepository.findById(ID))
                    .thenReturn(Optional.of(user));

            userService.deleteById(ID);

            verify(userRepository).findById(ID);
            verify(userRepository).delete(user);
        }

        @Test
        void shouldThrowExceptionWhenUserNotFound() {

            when(userRepository.findById(ID))
                    .thenReturn(Optional.empty());


            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.deleteById(ID)
            );


            verify(userRepository)
                    .findById(ID);

            verify(userRepository, never())
                    .delete(any());
        }
    }
}
