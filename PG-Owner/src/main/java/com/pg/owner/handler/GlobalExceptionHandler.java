package com.pg.owner.handler;

import com.pg.owner.custom_exception.ResourceExistException;
import com.pg.owner.custom_exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceExistException.class)
    public ResponseEntity<String> resourceExistExceptionHandler(ResourceExistException resourceExistException){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resourceExistException.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> resourceNotFoundExceptionHandler(ResourceNotFoundException resourceNotFoundException){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resourceNotFoundException.getMessage());
    }
}
