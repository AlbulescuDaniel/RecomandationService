package com.test.crypto.common.exceptions.handler;

import com.test.crypto.common.exceptions.BadRequestException;
import com.test.crypto.common.exceptions.InternalException;
import com.test.crypto.common.exceptions.NotFoundException;
import com.test.crypto.common.exceptions.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice("com.test.crypto")
public class RestErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public static ResponseEntity<ApiError> handleNotFoundException(
            NotFoundException exception) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public static ResponseEntity<ApiError> handleNotFoundException(
            BadRequestException exception) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InternalException.class)
    public static ResponseEntity<ApiError> handleInternalException(
            InternalException exception) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
