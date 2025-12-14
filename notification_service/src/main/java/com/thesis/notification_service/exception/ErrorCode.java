package com.thesis.notification_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(839999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(831001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(831005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(831006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(831007, "You do not have permission", HttpStatus.FORBIDDEN),
    BOOKINGS_NOT_EXISTED(831008, "Bookings not existed", HttpStatus.NOT_FOUND),
    CANNOT_SEND_EMAIL(832500, "Cannot send email", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}