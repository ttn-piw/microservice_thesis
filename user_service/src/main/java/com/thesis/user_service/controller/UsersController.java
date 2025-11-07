package com.thesis.user_service.controller;

import com.thesis.user_service.document.User;
import com.thesis.user_service.dto.request.RegisterRequest;
import com.thesis.user_service.dto.response.ApiResponse;
import com.thesis.user_service.dto.response.BookingUserResponse;
import com.thesis.user_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    @Autowired
    UserService userService;

    Logger log = LoggerFactory.getLogger(UsersController.class);

    @GetMapping("/test")
    public String testUserService() {
        return "Test from User Service!";
    }

    @GetMapping("/")
    public List<User> getStudents(){
        return userService.getAllStudents();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getUserById(HttpServletRequest request,@PathVariable("id") ObjectId id) {
        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null
                ? "?" + request.getQueryString()
                : "");

        log.info(requestPath);

        var user = userService.getUserById(id);

        if (user != null) {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .message("SUCCESS")
                    .data(user)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("FAIL")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/getBookingUserResponse")
    public BookingUserResponse getBookingUserResponse(@RequestParam("userId") String userId){
        return userService.getUserByUserId(userId);
    }

    @PostMapping("/createUser")
    public ResponseEntity<ApiResponse> registerAccount (HttpServletRequest http,@RequestBody RegisterRequest request){
        String path = http.getMethod() + " " + http.getRequestURI() + (http.getQueryString() != null ? "?" + http.getQueryString() : "") ;
        log.info(path);

        log.info(request.toString());
        ApiResponse response = userService.registerUser(request);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/updateInfo/{id}")
    public ResponseEntity<ApiResponse> updateUserInfo(HttpServletRequest http,@PathVariable("id") ObjectId id,@RequestBody RegisterRequest request){
        String path = http.getMethod() + " " + http.getRequestURI() + (http.getQueryString() != null ? "?" + http.getQueryString() : "") ;
        log.info(path);

        ApiResponse response = userService.updateInfoUser(id,request);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping(value= "/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") ObjectId id) {
        Boolean deletedUser = userService.deleteUserById(id);

        if (deletedUser){
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(200)
                    .message("SUCCESS")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            ApiResponse<Object> response = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("FAIL to delete user with id "+ id)
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
