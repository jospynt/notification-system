package org.example.service;

import org.example.dto.UserOperation;
import org.example.entity.User;
import org.example.event.UserEvent;
import org.example.exception.UserNotFoundException;
import org.example.exception.ValidationException;
import org.example.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public UserServiceImpl(
            UserRepository userRepository,
            ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Transactional
    public User create(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ValidationException("Email is already in use");
        }

        User savedUser = userRepository.save(user);

        applicationEventPublisher.publishEvent(
            new UserEvent(
                    UserOperation.CREATE,
                    savedUser.getEmail()
            )
        );

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User with id " + id + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User update(Long id, User user) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User with id " + id + " not found."));

        Optional<User> userWithSameEmail = userRepository.findByEmail(user.getEmail());

        if (userWithSameEmail.isPresent()
                && !userWithSameEmail.get().getId().equals(existingUser.getId())) {
            throw new ValidationException("Email is already in use.");
        }

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());

        User updatedUser = userRepository.save(existingUser);

        applicationEventPublisher.publishEvent(
            new UserEvent(
                    UserOperation.UPDATE,
                    updatedUser.getEmail()
            )
        );

        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User with id " + id + " not found."));

        userRepository.delete(user);

        applicationEventPublisher.publishEvent(
            new UserEvent(
                    UserOperation.DELETE,
                    user.getEmail()
            )
        );
    }
}