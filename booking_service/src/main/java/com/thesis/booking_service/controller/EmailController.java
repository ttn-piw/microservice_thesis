package com.thesis.booking_service.controller;

import com.thesis.booking_service.dto.request.SendEmailRequest;
import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.dto.response.EmailResponse;
import com.thesis.booking_service.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;

    @PostMapping("/api/v1/bookings/email/send")
    ApiResponse<EmailResponse> sendEmail(@RequestBody SendEmailRequest request){

        return ApiResponse.<EmailResponse>builder()
                .data(emailService.sendEmai(request))
                .build();
    }
}
