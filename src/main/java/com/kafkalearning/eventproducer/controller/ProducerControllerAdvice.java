package com.kafkalearning.eventproducer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ProducerControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<?> handleFailedRequests(MethodArgumentNotValidException argumentNotValidException) {

        List<FieldError> errorList = argumentNotValidException.getFieldErrors();
        String errorMsg = errorList.stream()
                .map(errorMessage -> errorMessage.getField() + "-" + errorMessage.getDefaultMessage())
                .sorted() // added so that the error messages can be returned in same order always. helps with validation/testing assertions.
                .collect(Collectors.joining(","));

        log.warn("error message: {}", errorMsg);
        return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
    }
}