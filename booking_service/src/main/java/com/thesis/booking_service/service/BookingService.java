package com.thesis.booking_service.service;

import com.thesis.booking_service.dto.request.CreateBookingRequest;
import com.thesis.booking_service.dto.request.GuestBookingRequest;
import com.thesis.booking_service.dto.request.RoomTypeBookingRequest;
import com.thesis.booking_service.dto.response.*;
import com.thesis.booking_service.exception.ErrorCode;
import com.thesis.booking_service.mapper.*;
import com.thesis.booking_service.model.BookedRoomType;
import com.thesis.booking_service.model.Booking;
import com.thesis.booking_service.model.BookingGuest;
import com.thesis.booking_service.repository.BookedRoomTypeRepository;
import com.thesis.booking_service.repository.BookingGuestRepository;
import com.thesis.booking_service.repository.BookingRepository;
import com.thesis.booking_service.repository.httpClient.authClient;
import com.thesis.booking_service.repository.httpClient.hotelClient;
import com.thesis.booking_service.repository.httpClient.userClient;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    BookingMapper bookingMapper;

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

    @Autowired
    RoomAvailableService roomAvailableService;

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
                .data(bookingMapper.toBookingResponse(bookingRepository.findBookingById(bookingId)))
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

    public ApiResponse getAdminOwnerBooking(String email){

        try{
            List<UUID> listHotelId = hotelClient.getHotelIdByOwnerId(email);//Owner_id
            List<Booking> bookingList = new ArrayList<>();

            listHotelId.forEach(id -> {
                log.info("Id: {}", id);
                List<Booking> bookings = bookingRepository.findBookingByHotelId(id);
                bookingList.addAll(bookings);
            });

            return ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .message("SUCCESSFUL")
                    .data(bookingList)
                    .build();

        } catch (FeignException e){
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
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

    @Transactional
    public ApiResponse bookingRoom(CreateBookingRequest request, String email) {
            LocalDate now = LocalDate.now();
            if (request.getCheckInDate().isBefore(now))
                return ApiResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message("Check in date must be after today!")
                        .build();

            if (request.getCheckInDate().isAfter(request.getCheckOutDate()))
                return ApiResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message("Check out must after check in!")
                        .build();

            Booking booking = new Booking();
//            log.info("Booking request info: {}", request.toString());

            //GET user_id from auth -> phone
            String userId = authClient.getUserId(email);
            BookingUserResponse user = userClient.getBookingUserResponse(userId);
            booking.setUserId(user.getUserId());
            booking.setUserEmail(email);
            booking.setUser_phone(user.getPhone());

            String getHotelName = hotelClient.getHotelName(request.getHotelId());
            booking.setHotelId(request.getHotelId());
            booking.setHotel_name_snapshot(getHotelName);
            booking.setCheck_in_date(request.getCheckInDate());
            booking.setCheck_out_date(request.getCheckOutDate());
            booking.setSpecial_requests(request.getSpecialRequests());
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPaymentStatus(PaymentStatusType.PENDING);
            booking.setCreated_at(OffsetDateTime.now());
            booking.setUpdated_at(OffsetDateTime.now());

            List<BookedRoomType> bookedRoomTypeList = new ArrayList<>();
            List<BookingGuest> bookingGuestList = new ArrayList<>();

            booking.setBookedRoomTypes(bookedRoomTypeList);
            booking.setBookingGuests(bookingGuestList);

            //Handle total_price without check available room
            long numberOfNights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
//            log.info("Number of night: {}", numberOfNights);
            BigDecimal total_price = BigDecimal.ZERO;

            for (RoomTypeBookingRequest roomType : request.getRoomTypes()) {
                //Check room available
                roomAvailableService.checkAvailability(
                        roomType.getRoomTypeId(),
                        request.getCheckInDate(),
                        request.getCheckOutDate(),
                        roomType.getQuantity()
                );

                RoomTypeResponse roomInfo = hotelClient.getRoomTypeResponse(roomType.getRoomTypeId());

                BigDecimal roomTotalPrice = BigDecimal.valueOf(roomInfo.getPrice_per_night())
                        .multiply(BigDecimal.valueOf(roomType.getQuantity()))
                        .multiply(BigDecimal.valueOf(numberOfNights));
                total_price = total_price.add(roomTotalPrice);

                BookedRoomType newBookedRoomType = new BookedRoomType();
                newBookedRoomType.setRoom_type_id(roomType.getRoomTypeId());
                newBookedRoomType.setRoom_type_name_snapshot(roomInfo.getName());
                newBookedRoomType.setQuantity(roomType.getQuantity());
                newBookedRoomType.setPrice_per_night_snapshot(roomInfo.getPrice_per_night());
                newBookedRoomType.setBooking(booking);

                bookedRoomTypeList.add(newBookedRoomType);
            }

            for (GuestBookingRequest guest : request.getGuests()){
                BookingGuest newBookingGuest = new BookingGuest();
                newBookingGuest.setBooking(booking);
                newBookingGuest.setFull_name(guest.getFull_name());
                newBookingGuest.setEmail(guest.getEmail());
                newBookingGuest.setIs_primary(guest.getIs_primary());

                bookingGuestList.add(newBookingGuest);
            }

            booking.setTotal_price(total_price.doubleValue());
            String payment_intent_id = "pi" + "_" + userId.toString() + request.getCheckInDate() + request.getCheckOutDate();
            booking.setPayment_intent_id(payment_intent_id);

//            log.info("Booking: {}", booking);
            bookingRepository.save(booking);

            BookingSuccessResponse response = new BookingSuccessResponse();
            response.setId(booking.getId());

            return ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .message(booking.getId().toString())
                    .data(response)
                    .build();
    }

    public ApiResponse getAllBookingsClient(String email){
        List<Booking> getAllBookings = bookingRepository.findBookingByUserEmail(email);

        if (getAllBookings.isEmpty())
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format(("No bookings found with email: %s not found"),email))
                    .build();

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESSFUL: Booking detail")
                .data(getAllBookings)
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
        response.setHotel_id(getBookingInfo.getHotelId());
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

    public ApiResponse deleteBooking(UUID id){
        Booking getBooking = bookingRepository.findBookingById(id);

        if (getBooking == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("FAIL: Booking with %s not found", id))
                    .data(null)
                    .build();

        bookingRepository.deleteById(id);

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(String.format("SUCCESSFULLY: Delete booking with id: %s ", id))
                .build();
    }

//    INTERNAL METHOD
    public Map<UUID,Integer> getNumberBookedRoom(UUID hotelId, LocalDate checkIn, LocalDate checkOut){
        List<BookedCountResponse> result;

        String status = BookingStatus.CONFIRMED.name();
        result = bookingRepository.findBookedRoomCountsNative(hotelId,status,checkIn,checkOut);

        return result.stream()
                .collect(Collectors.toMap(
                        BookedCountResponse::getRoomTypeId,
                        BookedCountResponse::getBookedCount
                ));
    }

    public List<UUID> getPopularHotels(){
        return bookingRepository.getPopularHotelsID();
    }

    public List<SendToReviewResponse> getBookingByEmailToReview(String email){
        List<Booking> getAllBookings = bookingRepository.findBookingByUserEmail(email);
//        log.info("Booking list: {}", getAllBookings);

        return getAllBookings.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private SendToReviewResponse convertToDto(Booking booking) {
        // Map Room Type
        List<BookedRoomTypeDTO> roomDtos = booking.getBookedRoomTypes().stream()
                .map(room -> BookedRoomTypeDTO.builder()
                        .room_type_id(room.getRoom_type_id())
                        .room_type_name_snapshot(room.getRoom_type_name_snapshot())
                        .quantity(room.getQuantity())
                        .price_per_night_snapshot(room.getPrice_per_night_snapshot())
                        .build())
                .toList();

        // Map Guest
        List<BookingGuestDTO> guestDtos = booking.getBookingGuests().stream()
                .map(guest -> BookingGuestDTO.builder()
                        .full_name(guest.getFull_name())
                        .email(guest.getEmail())
                        .is_primary(guest.getIs_primary())
                        .build())
                .toList();

        return SendToReviewResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .userEmail(booking.getUserEmail())
                .userPhone(booking.getUser_phone())
                .hotelId(booking.getHotelId())
                .hotel_name_snapshot(booking.getHotel_name_snapshot())
                .check_in_date(booking.getCheck_in_date())
                .check_out_date(booking.getCheck_out_date())
                .total_price(booking.getTotal_price())
                .special_requests(booking.getSpecial_requests())
                .status(booking.getStatus())
                .created_at(booking.getCreated_at())
                .updated_at(booking.getUpdated_at())
                .paymentStatus(booking.getPaymentStatus())
                .payment_intent_id(booking.getPayment_intent_id())
                .bookedRoomTypes(roomDtos)
                .bookingGuests(guestDtos)
                .build();
    }
}
