package org.example.service;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail =
            new GreenMailExtension(ServerSetupTest.SMTP);

    @Autowired
    private EmailService emailService;

    @Test
    void shouldSendAccountCreatedEmail() throws Exception {

        emailService.sendAccountCreatedEmail("test@mail.com");

        Message[] messages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);

        Message message = messages[0];

        assertThat(message.getSubject())
                .isEqualTo("Account created");
    }

    @Test
    void shouldSendAccountDeletedEmail() throws Exception {

        emailService.sendAccountDeletedEmail("test@mail.com");

        Message[] messages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);

        assertThat(messages[0].getSubject())
                .isEqualTo("Account deleted");
    }
}