package org.example.listener;

import org.example.dto.UserNotificationEvent;
import org.example.event.UserEvent;
import org.example.kafka.UserEventProducer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
public class UserEventListener {

    private final UserEventProducer userEventProducer;


    public UserEventListener(UserEventProducer userEventProducer) {
        this.userEventProducer = userEventProducer;
    }


    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleUserEvent(UserEvent event) {

        userEventProducer.sendUserEvent(
                new UserNotificationEvent(
                        event.operation(),
                        event.email()
                )
        );
    }
}