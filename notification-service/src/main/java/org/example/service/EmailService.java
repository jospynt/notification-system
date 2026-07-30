package org.example.service;

public interface EmailService {

    void sendAccountCreatedEmail(String email);

    void sendAccountDeletedEmail(String email);

    void send(String email, String subject, String message);
}
