package com.shop.akisshop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.shop.akisshop.response.Response;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
        public final ResponseEntity<Response<String>> handleDataNotFoundException (DataNotFoundException ex) {
            return Response.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        }
}
