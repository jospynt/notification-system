package org.example.kafka;

import org.example.dto.UserNotificationEvent;
import org.example.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private final EmailService emailService;

    public NotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(
            topics = "user-events",
            groupId = "notification-group"
    )
    public void consume(UserNotificationEvent event) {

        switch (event.operation()) {

            case CREATE ->
                emailService.sendAccountCreatedEmail(event.email());

            case DELETE ->
                emailService.sendAccountDeletedEmail(event.email());
        }
    }
}
