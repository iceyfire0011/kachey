package com.solution.kachey.config.exception;

import com.solution.kachey.config.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.solution.kachey.user_manager.exception.InvalidRoleException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle InvalidRoleException
    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<Object> handleInvalidRoleException(InvalidRoleException ex) {
        // Create an error response object
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
    }
    
 // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}