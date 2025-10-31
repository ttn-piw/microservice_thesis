package com.thesis.booking_service.service;

import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.dto.response.BookedRoomTypeDTO;
import com.thesis.booking_service.dto.response.BookingDetailsResponse;
import com.thesis.booking_service.dto.response.BookingGuestDTO;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
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
    public ApiResponse getBookingByUserId(UUID userId) {
        if (!bookingRepository.existsByUserId(userId)) {
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("FAIL: User with %s not found", userId))
                    .data(null)
                    .build();
        }

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESSFUL")
                .data(bookingRepository.findByUserId(userId))
                .build();
    }

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
}
