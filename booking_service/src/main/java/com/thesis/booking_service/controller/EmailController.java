package com.thesis.booking_service.controller;

import com.thesis.booking_service.dto.request.SendEmailRequest;
import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.dto.response.BookingResponse;
import com.thesis.booking_service.dto.response.EmailResponse;
import com.thesis.booking_service.exception.AppException;
import com.thesis.booking_service.exception.ErrorCode;
import com.thesis.booking_service.mapper.BookingMapper;
import com.thesis.booking_service.model.Booking;
import com.thesis.booking_service.repository.BookingRepository;
import com.thesis.booking_service.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingMapper bookingMapper;

    @PostMapping("/api/v1/bookings/email/send")
    ApiResponse<EmailResponse> sendEmail(@RequestBody SendEmailRequest request){

        return ApiResponse.<EmailResponse>builder()
                .data(emailService.sendEmai(request))
                .build();
    }

    @PostMapping("/api/v1/bookings/email/chatbot/{bookingId}")
    public void sendEmailWithChatBot(@PathVariable UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKINGS_NOT_EXISTED));

        BookingResponse response = bookingMapper.toBookingResponse(booking);
        log.info("Booking chat bot: {}", booking);

        emailService.sendBotBookingConfirmation(response);
    }
}
