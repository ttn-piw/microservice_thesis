package com.thesis.notification_service.controller;

import com.thesis.event.dto.NotificationEvent;
import com.thesis.notification_service.dto.request.Recipient;
import com.thesis.notification_service.dto.request.SendEmailRequest;
import com.thesis.notification_service.dto.response.ApiResponse;
import com.thesis.notification_service.dto.response.EmailResponse;
import com.thesis.notification_service.service.EmailService;
import com.thesis.notification_service.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@Slf4j
public class NotificationController {
    @Autowired
    EmailService emailService;

    @Autowired
    TemplateService templateService;

    @KafkaListener(topics = "notification-delivery")
    public void listenNotificationDelivery(NotificationEvent message){
        log.info("Message: {}", message);

        String htmlContent = "";

        if ("BOOKING_SUCCESS_TEMPLATE".equals(message.getTemplateCode())) {
            htmlContent = templateService.generateContent("email-booking-success.html", message.getParam());
        } else {
            htmlContent = message.getBody();
        }

        emailService.sendEmail(SendEmailRequest.builder()
                        .to(Recipient.builder()
                                .email(message.getRecipient())
                                .name(message.getParam() != null ? (String) message.getParam().get("guestName") : "")
                                .build())
                        .subject(message.getSubject())
                        .htmlContent(htmlContent)
                        .build());
        log.info("Email sent to {}", message.getRecipient());
    }
}
