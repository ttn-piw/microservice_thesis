package com.thesis.booking_service.service;

import com.thesis.booking_service.dto.request.EmailRequest;
import com.thesis.booking_service.dto.request.Recipient;
import com.thesis.booking_service.dto.request.SendEmailRequest;
import com.thesis.booking_service.dto.request.Sender;
import com.thesis.booking_service.dto.response.BookingResponse;
import com.thesis.booking_service.dto.response.EmailResponse;
import com.thesis.booking_service.exception.AppException;
import com.thesis.booking_service.exception.ErrorCode;
import com.thesis.booking_service.model.Booking;
import com.thesis.booking_service.repository.httpClient.emailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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

    private static final String EMAIL_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Booking Confirmation</title>
                <style>
                    body { margin: 0; padding: 0; background-color: #f4f4f4; font-family: 'Arial', sans-serif; }
                    table { border-spacing: 0; border-collapse: collapse; }
                    img { border: 0; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }
                    @media screen and (max-width: 600px) {
                        .container { width: 100% !important; }
                        .mobile-stack { display: block !important; width: 100% !important; }
                        .mobile-padding { padding: 20px 15px !important; }
                        .header-text { font-size: 24px !important; }
                    }
                </style>
            </head>
            <body style="margin: 0; padding: 0; background-color: #f4f4f4;">
                <table border="0" cellpadding="0" cellspacing="0" width="100%" style="background-color: #f4f4f4;">
                    <tr>
                        <td align="center" style="padding: 20px 0;">
                            <table border="0" cellpadding="0" cellspacing="0" width="600" style="background-color: #ffffff; border-radius: 8px; overflow: hidden;">
                                <tr>
                                    <td align="center" style="background-color: #5392f9; padding: 40px 20px;">
                                        <h1 style="color: #ffffff; font-size: 28px;">TNEcoHotel</h1>
                                        <p style="color: #e0f0ff;">Booking Confirmation</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 40px 40px 20px 40px; text-align: center;">
                                        <h2 style="color: #333;">Thank you, {{guestName}}!</h2>
                                        <p>Your booking at <strong>{{hotelName}}</strong> has been confirmed.</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding: 15px;">
                                        <span style="color: #5392f9; font-size: 24px; font-weight: bold;">{{bookingId}}</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 30px 40px;">
                                        <p><strong>Check-in:</strong> {{checkInDate}}</p>
                                        <p><strong>Check-out:</strong> {{checkOutDate}}</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 0 40px;">
                                        <h3>Room Details</h3>
                                        <p>{{roomDetails}}</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 20px 40px;">
                                        <h3>TOTAL PRICE: <span style="color: #5392f9;">{{totalPrice}}</span></h3>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 0 40px 30px 40px;">
                                        <p><strong>Special Requests:</strong> {{specialRequests}}</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """;

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

    public void sendBotBookingConfirmation(BookingResponse booking) {

        NumberFormat currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormatter.format(booking.getTotal_price()) + " VND";

        long nights = ChronoUnit.DAYS.between(booking.getCheck_in_date(), booking.getCheck_out_date());
        if (nights < 1) nights = 1;

        long finalNights = nights;
        String roomDetailsHtml = booking.getBookedRoomTypes().stream()
                .map(rt -> rt.getQuantity() + "x " + rt.getRoom_type_name_snapshot() + " (" + finalNights + " Nights)")
                .collect(Collectors.joining("<br>"));

        String finalHtml = EMAIL_TEMPLATE
                .replace("{{guestName}}", booking.getUserEmail())
                .replace("{{hotelName}}", booking.getHotel_name_snapshot())
                .replace("{{bookingId}}", booking.getId().toString())
                .replace("{{checkInDate}}", booking.getCheck_in_date().toString())
                .replace("{{checkOutDate}}", booking.getCheck_out_date().toString())
                .replace("{{roomDetails}}", roomDetailsHtml)
                .replace("{{totalPrice}}", formattedPrice)
                .replace("{{specialRequests}}", booking.getSpecial_requests() != null ? booking.getSpecial_requests() : "None");

        SendEmailRequest request = SendEmailRequest.builder()
                .to(Recipient.builder()
                        .email(booking.getUserEmail())
                        .name(booking.getBookingGuests().get(0).getFull_name()).build())
                .subject("Booking Confirmed: " + booking.getHotel_name_snapshot() + " (Ref: " + booking.getId() + ")")
                .htmlContent(finalHtml)
                .build();

        this.sendEmai(request);
    }
}
