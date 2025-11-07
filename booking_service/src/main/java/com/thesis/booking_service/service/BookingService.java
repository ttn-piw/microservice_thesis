package com.thesis.booking_service.service;

import com.thesis.booking_service.dto.request.CreateBookingRequest;
import com.thesis.booking_service.dto.request.GuestBookingRequest;
import com.thesis.booking_service.dto.request.RoomTypeBookingRequest;
import com.thesis.booking_service.dto.response.*;
import com.thesis.booking_service.exception.ErrorCode;
import com.thesis.booking_service.mapper.BookedRoomTypeMapper;
import com.thesis.booking_service.mapper.BookingGuestMapper;
import com.thesis.booking_service.mapper.BookingStatus;
import com.thesis.booking_service.mapper.PaymentStatusType;
import com.thesis.booking_service.model.BookedRoomType;
import com.thesis.booking_service.model.Booking;
import com.thesis.booking_service.model.BookingGuest;
import com.thesis.booking_service.repository.BookedRoomTypeRepository;
import com.thesis.booking_service.repository.BookingGuestRepository;
import com.thesis.booking_service.repository.BookingRepository;
import com.thesis.booking_service.repository.httpClient.authClient;
import com.thesis.booking_service.repository.httpClient.hotelClient;
import com.thesis.booking_service.repository.httpClient.userClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    Logger log = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookedRoomTypeRepository bookedRoomType;

    @Autowired
    BookingGuestRepository bookingGuest;

    @Autowired
    BookedRoomTypeMapper bookedRoomTypeMapper;

    @Autowired
    BookingGuestMapper bookingGuestMapper;

    @Autowired
    hotelClient hotelClient;

    @Autowired
    userClient userClient;

    @Autowired
    authClient authClient;

    public ApiResponse getAllBookings(){
        return ApiResponse.builder()
                .code(200)
                .message("SUCCESSFULLY:")
                .data(bookingRepository.findAll())
                .build();
    }

    public ApiResponse getBookingById(UUID bookingId){
        if (bookingRepository.findBookingById(bookingId) == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("FAIL: Booking with %s not found",bookingId.toString()))
                    .data(null)
                    .build();

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESSFUL")
                .data(bookingRepository.findBookingById(bookingId))
                .build();
    }
//    public ApiResponse getBookingByUserId(UUID userId) {
//        if (!bookingRepository.existsByUserId(userId)) {
//            return ApiResponse.builder()
//                    .code(HttpStatus.NOT_FOUND.value())
//                    .message(String.format("FAIL: User with %s not found", userId))
//                    .data(null)
//                    .build();
//        }
//
//        return ApiResponse.builder()
//                .code(HttpStatus.OK.value())
//                .message("SUCCESSFUL")
//                .data(bookingRepository.findByUserId(userId))
//                .build();
//    }

    public ApiResponse cancelBooking(UUID id){
        Booking takeBookingInfo = bookingRepository.findBookingById(id);
        LocalDate now = LocalDate.now();
        log.info("takeBookingInfo {}",takeBookingInfo.toString());

        if (takeBookingInfo == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("FAIL: Booking with %s not found", id))
                    .data(null)
                    .build();

        if (!takeBookingInfo.getCheck_in_date().isAfter(now))
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("FAIL: Check_in_date must not be late than today")
                    .build();

        if (takeBookingInfo.getPaymentStatus() == PaymentStatusType.SUCCESSFUL)
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Cannot delete: Your payment process must not be different with SUCCESSFUL")
                    .build();

        takeBookingInfo.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(takeBookingInfo);

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(String.format("SUCCESSFULLY: Cancel booking with id: %s ", id))
                .build();
    }

    public ApiResponse getBookingOfUser(String email){
        List<Booking> getBookings = bookingRepository.findByUserEmail(email);
        if (getBookings.isEmpty())
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format(("Bookings with email: %s not found"),email))
                    .build();

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESSFUL: List of bookings")
                .data(getBookings)
                .build();
    }

    public ApiResponse bookingRoom(CreateBookingRequest request, String email) {
        {
//            "hotelId": "b779b2a1-1afc-4778-a9d8-efd14167577a",
//                "checkInDate": "2025-12-20",
//                "checkOutDate": "2025-12-25",
//                "specialRequests": "Phòng tầng cao, không hút thuốc.",
//                "guests": [
//            { "full_name": "Nguyen Van A", "email": "a@gmail.com", "is_primary": true },
//            { "full_name": "Tran Thi B", "is_primary": false }
//  ],
//            "roomTypes": [
//            { "roomTypeId": "c8e5f84e-2b21-4b18-9d41-525d6a8e8e78", "quantity": 1 },
//            { "roomTypeId": "f7d9e0c3-1a2b-4c5d-8e6f-7a8b9c0d1e2f", "quantity": 2 }
//  ]
//        }
            Booking booking = new Booking();
            log.info("Booking request info: {}", request.toString());

            //GET user_id from auth -> phone
            String userId = authClient.getUserId(email);
            BookingUserResponse user = userClient.getBookingUserResponse(userId);
            booking.setUserId(user.getUserId());
            booking.setUserEmail(email);
            booking.setUser_phone(user.getPhone());

            String getHotelName = hotelClient.getHotelName(request.getHotelId());
            booking.setHotel_id(request.getHotelId());
            booking.setHotel_name_snapshot(getHotelName);
            booking.setCheck_in_date(request.getCheckInDate());
            booking.setCheck_out_date(request.getCheckOutDate());
            booking.setSpecial_requests(request.getSpecialRequests());
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPaymentStatus(PaymentStatusType.PENDING);
            booking.setCreated_at(OffsetDateTime.now());
            booking.setUpdated_at(OffsetDateTime.now());

            //Handle total_price without check available room
            long numberOfNights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
            log.info("Number of night: {}", numberOfNights);
            BigDecimal total_price = BigDecimal.ZERO;
            for (RoomTypeBookingRequest roomType : request.getRoomTypes()) {
                BigDecimal roomTotalPrice = BigDecimal.valueOf(hotelClient.getPrice(roomType.getRoomTypeId()))
                        .multiply(BigDecimal.valueOf(roomType.getQuantity()))
                        .multiply(BigDecimal.valueOf(numberOfNights));
                total_price = total_price.add(roomTotalPrice);
            }
            booking.setTotal_price(total_price.doubleValue());
            log.info("Booking: {}", booking);

            return ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .message("SUCCESSFUL: New booking created")
                    .data(null)
                    .build();
        }
    }

    public ApiResponse getBookingDetailOfUser(String email, UUID id){
        Booking getBookingInfo = bookingRepository.findBookingByIdAndUserEmail(id,email);
        if (getBookingInfo == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format(("No bookings found with email: %s not found"),email))
                    .build();

        List<BookedRoomType> listBookedRoomType = bookedRoomType.findBookedRoomTypeByBookingId(id);
        List<BookedRoomTypeDTO> roomTypes= listBookedRoomType
                .stream()
                .map(bookedRoomTypeMapper::toBookedRoomTypeDTO)
                .toList();

        List<BookingGuest> listBookingGuests = bookingGuest.findBookingGuestByBookingId(id);
        log.info("List booking guests: {}", listBookingGuests);

        List<BookingGuestDTO> guests = listBookingGuests
                .stream()
                .map(bookingGuestMapper::toBookingGuestDTO)
                .toList();

        BookingDetailsResponse response = new BookingDetailsResponse();
        response.setId(id);
        response.setStatus(getBookingInfo.getStatus());
        response.setPaymentStatus(getBookingInfo.getPaymentStatus());
        response.setCheck_in_date(getBookingInfo.getCheck_in_date());
        response.setCheck_out_date(getBookingInfo.getCheck_out_date());
        response.setTotalPrice(getBookingInfo.getTotal_price());
        response.setSpecial_requests(getBookingInfo.getSpecial_requests());
        response.setCreated_at(getBookingInfo.getCreated_at());
        response.setHotel_id(getBookingInfo.getHotel_id());
        response.setHotel_name_snapshot(getBookingInfo.getHotel_name_snapshot());

        response.setRoomTypes(roomTypes);
        response.setGuests(guests);

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESSFUL: Booking detail")
                .data(response)
                .build();
    }

    public ApiResponse cancelBookingByUser(String email, UUID bookingId){
        Booking getBookingInfo = bookingRepository.findBookingByIdAndUserEmail(bookingId,email);


        LocalDate now = LocalDate.now();
        log.info("getBookingInfo {}",getBookingInfo.toString());

        if (getBookingInfo == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("FAIL: Booking with %s not found", bookingId))
                    .data(null)
                    .build();

        if (!getBookingInfo.getCheck_in_date().isAfter(now))
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("FAIL: Check_in_date must not be late than today")
                    .build();

        if (getBookingInfo.getPaymentStatus() == PaymentStatusType.SUCCESSFUL)
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("CANNOT DELETE: Your bookings has been paid SUCCESSFUL!")
                    .build();

        getBookingInfo.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(getBookingInfo);

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(String.format("SUCCESSFULLY: Cancel booking with id: %s ", bookingId))
                .build();
    }
}
