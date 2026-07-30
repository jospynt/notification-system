package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.NotificationRequest;
import org.example.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendEmail(
            @Valid @RequestBody NotificationRequest request) {

        emailService.send(
                request.email(),
                request.subject(),
                request.text()
        );

        return ResponseEntity.ok().build();
    }

}
