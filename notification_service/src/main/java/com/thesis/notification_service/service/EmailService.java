package com.thesis.notification_service.service;

import com.thesis.notification_service.dto.request.EmailRequest;
import com.thesis.notification_service.dto.request.Recipient;
import com.thesis.notification_service.dto.request.SendEmailRequest;
import com.thesis.notification_service.dto.request.Sender;
import com.thesis.notification_service.dto.response.EmailResponse;
import com.thesis.notification_service.exception.AppException;
import com.thesis.notification_service.exception.ErrorCode;
import com.thesis.notification_service.repository.httpClient.emailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailService {
    @Autowired
    emailClient emailClient;

    @Value("${API_EMAILKEY}")
    String apiKey;

    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("TNEcoHotel")
                        .email("ttrungnguyen2003@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();
        log.info("Email request: {}", emailRequest);
        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e) {
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }

}
