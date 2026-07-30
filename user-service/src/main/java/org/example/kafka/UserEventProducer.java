package org.example.kafka;

import org.example.dto.UserNotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {

    private static final String TOPIC = "user-events";

    private final KafkaTemplate<String, UserNotificationEvent> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, UserNotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserEvent(UserNotificationEvent event) {

        try {
            kafkaTemplate.send(TOPIC, event).get();
            System.out.println("Kafka event sent: " + event);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}