package com.example.imagehosting.exception;


import com.example.imagehosting.dto.ApiResponse;
import com.example.imagehosting.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse> handleRegistrationException(InvalidInputException e){
        ApiResponse res = new ApiResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException e){
        ApiResponse res = new ApiResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e){
        ErrorResponse res = new ErrorResponse();
        res.setStatus(HttpStatus.BAD_REQUEST);
        res.setErrorCount(e.getBindingResult().getErrorCount());
        res.setMessages(e.getBindingResult().getAllErrors().stream().map(objectError -> new String(objectError.getDefaultMessage())).collect(Collectors.toList()));
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintValidationException(ConstraintViolationException e){
        ErrorResponse res = new ErrorResponse();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> violations = constraintViolations.stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.toList());

        res.setStatus(HttpStatus.BAD_REQUEST);
        res.setErrorCount(violations.size());
        res.setMessages(violations);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        return new ResponseEntity<Object>("Access denied message here", new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

}
