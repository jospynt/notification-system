package org.example.service;

import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(User user);
    User findUserById(Long id);
    List<User> findAll();
    User update(Long id, User user);
    void deleteById(Long id);
}
