package com.thesis.booking_service.service;

import com.thesis.booking_service.dto.request.EmailRequest;
import com.thesis.booking_service.dto.request.SendEmailRequest;
import com.thesis.booking_service.dto.request.Sender;
import com.thesis.booking_service.dto.response.EmailResponse;
import com.thesis.booking_service.exception.AppException;
import com.thesis.booking_service.exception.ErrorCode;
import com.thesis.booking_service.repository.httpClient.emailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    emailClient emailClient;

    @Value("${api.emailKey}")
    String apiKey;

    public EmailResponse sendEmai(SendEmailRequest request) {
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
