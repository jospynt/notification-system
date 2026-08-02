package org.example.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendAccountCreatedEmail(String email) {
        send(
                email,
                "Account created",
                "Здравствуйте! Ваш аккаунт на сайте был успешно создан."
        );
    }

    @Override
    public void sendAccountDeletedEmail(String email) {
        send(
                email,
                "Account deleted",
                "Ваш аккаунт был удалён."
        );
    }

    @Override
    public void send(String email, String subject, String text) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(email);
        mail.setSubject(subject);
        mail.setText(text);

        mailSender.send(mail);
    }
}
