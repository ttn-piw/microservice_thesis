package com.thesis.user_service.service;

import com.thesis.user_service.document.User;
import com.thesis.user_service.dto.request.RegisterRequest;
import com.thesis.user_service.dto.request.UserUpdateRequest;
import com.thesis.user_service.dto.response.ApiResponse;
import com.thesis.user_service.dto.response.BookingUserResponse;
import com.thesis.user_service.dto.response.UserResponseById;
import com.thesis.user_service.mapper.bookingInfoUserMapper;
import com.thesis.user_service.repository.UserRepository;
import com.thesis.user_service.repository.httpClient.authClient;
import feign.FeignException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    bookingInfoUserMapper userMapper;

    @Autowired
    authClient authClient;

    Logger log = LoggerFactory.getLogger(UserService.class);

    public List<User> getAllStudents(){
        return userRepository.findAll();
    }

    public UserResponseById getUserById(ObjectId id){
        User user = userRepository.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //DTO user -> UserResponse
        UserResponseById result = new UserResponseById(user.getName(), user.getGender(), user.getPhone(), user.getAvatar(), user.getBirthday().toString());
        return result;
    }

    public ApiResponse getUserByEmail(String email){
        try {
            String userId = authClient.getUserId(email);
            log.info("userId: {}", userId);

            User user = userRepository.getUserByUserId(userId);
            if (user == null)
                return ApiResponse.builder()
                        .code(404)
                        .message("User not found")
                        .data(null)
                        .build();

            return ApiResponse.builder()
                    .code(200)
                    .message("SUCCESSFUL")
                    .data(user)
                    .build();

        } catch (FeignException e) {
            return ApiResponse.builder()
                    .code(500)
                    .message("Error when connect to auth client")
                    .build();
        }
    }

    public BookingUserResponse getUserByUserId(String userId){
        User user = userRepository.getUserByUserId(userId);
        BookingUserResponse response = userMapper.toBookingUserResponse(user);
        return response;
    }

    public ApiResponse registerUser(RegisterRequest request){
        //Check exist account
        if (userRepository.getUserByPhone(request.getPhone()) != null) {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(HttpStatus.BAD_GATEWAY.value())
                    .data(null)
                    .message("Phone has been existed")
                    .build();
           return response;
        }

       var new_user = new User();
       new_user.setName(request.getName());
       new_user.setGender(request.getGender());
       new_user.setPhone(request.getPhone());
       new_user.setAvatar(request.getAvatar());
       new_user.setBirthday(request.getBirthday());
       new_user.setUserId(request.getUser_id());

       log.info(new_user.toString());

       userRepository.save(new_user);

       return ApiResponse.builder().code(HttpStatus.OK.value()).data(null).message("SUCCESS").build();
    }

    public ApiResponse updateInfoUser(ObjectId id, RegisterRequest request){
        Optional<User> exist_user = userRepository.findById(id.toString());
        if (exist_user.isPresent()) {
            return ApiResponse.<User>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("User not found")
                    .data(null)
                    .build();
        } else {
            var update_user = exist_user.get();
            update_user.setName(request.getName());
            update_user.setGender(request.getGender());
            update_user.setPhone(request.getPhone());
            update_user.setAvatar(request.getAvatar());
            update_user.setBirthday(request.getBirthday());

            userRepository.save(update_user);

           return ApiResponse.<User>builder()
                    .code(HttpStatus.OK.value())
                    .message("Success")
                    .data(exist_user.get())
                    .build();
        }
    }

    public ApiResponse updateInfoUserByEmail(String email, UserUpdateRequest request){
        try {
            String userId = authClient.getUserId(email);
            Optional<User> exist_user = userRepository.findByUserId(userId);

            if (exist_user.isPresent()) {
                var update_user = exist_user.get();

                update_user.setName(request.getName());
                update_user.setGender(request.getGender());
                update_user.setPhone(request.getPhone());
                if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
                    update_user.setAvatar(request.getAvatar());
                }
                update_user.setBirthday(request.getDob());
                userRepository.save(update_user);

                return ApiResponse.<User>builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(update_user)
                        .build();
            } else {
                return ApiResponse.<User>builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message("User not found with ID: " + userId)
                        .data(null)
                        .build();
            }
        } catch (FeignException e) {
            return ApiResponse.builder()
                    .code(500)
                    .message("Error when connect to auth client")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .code(500)
                    .message("Internal Server Error: " + e.getMessage())
                    .build();
        }
    }

    public Boolean deleteUserById(ObjectId id){
        User user = userRepository.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user != null){
            userRepository.deleteById(user.getId());
            return true;
        } else
            return false;
    }
}
