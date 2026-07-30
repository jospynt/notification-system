package org.example.service;

import org.example.dto.UserNotificationEvent;
import org.example.dto.UserOperation;
import org.example.entity.User;
import org.example.exception.UserNotFoundException;
import org.example.exception.ValidationException;
import org.example.kafka.UserEventProducer;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    public UserServiceImpl(
            UserRepository userRepository,
            UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.userEventProducer = userEventProducer;
    }

    @Override
    @Transactional
    public User create(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ValidationException("Email is already in use");
        }

        User savedUser = userRepository.save(user);

        userEventProducer.sendUserEvent(
                new UserNotificationEvent(
                        UserOperation.CREATE,
                        savedUser.getEmail()
                )
        );

        return userRepository.save(user);
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

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User with id " + id + " not found."));

        userRepository.delete(user);

        userEventProducer.sendUserEvent(
                new UserNotificationEvent(
                        UserOperation.DELETE,
                        user.getEmail()
                )
        );
    }
}