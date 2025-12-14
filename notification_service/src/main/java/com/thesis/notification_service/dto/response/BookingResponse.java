//package com.thesis.notification_service.dto.response;
//
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//
//import java.time.LocalDate;
//import java.time.OffsetDateTime;
//import java.util.List;
//import java.util.UUID;
//
//@Builder
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class BookingResponse {
//    UUID id;
//    String userId;
//    String userEmail;
//    String user_phone;
//    UUID hotelId;
//    String hotel_name_snapshot;
//    LocalDate check_in_date;
//    LocalDate check_out_date;
//    Double total_price;
//    String special_requests;
//    private BookingStatus status;
//    private PaymentStatusType paymentStatus;
//    String payment_intent_id;
//    OffsetDateTime created_at;
//    OffsetDateTime updated_at;
//    private List<BookingGuest> bookingGuests;
//    private List<BookedRoomType> bookedRoomTypes;
//}
