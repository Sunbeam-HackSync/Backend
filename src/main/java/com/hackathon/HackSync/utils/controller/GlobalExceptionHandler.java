package com.hackathon.HackSync.utils.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hackathon.HackSync.utils.dto.ErrorResponse;
import com.hackathon.HackSync.utils.exception.AccessDeniedException;
import com.hackathon.HackSync.utils.exception.AlreadyVerifiedException;
import com.hackathon.HackSync.utils.exception.InvalidOTPException;
import com.hackathon.HackSync.utils.exception.InvalidRefreshTokenException;
import com.hackathon.HackSync.utils.exception.ResourceNotFoundException;

import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().status(HttpStatus.UNAUTHORIZED)
                        .message("JWT access token has expired. Please refresh the token.").build(),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().status(HttpStatus.UNAUTHORIZED).message("JWT token is invalid.").build(),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().status(HttpStatus.NOT_FOUND).message(ex.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().status(HttpStatus.UNAUTHORIZED).message(ex.getMessage()).build(),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException(DisabledException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().status(HttpStatus.UNAUTHORIZED).message(ex.getMessage()).build(),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidOTPException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOTPException(InvalidOTPException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().status(HttpStatus.UNAUTHORIZED).message(ex.getMessage()).build(),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyVerifiedException(AlreadyVerifiedException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().status(HttpStatus.BAD_REQUEST).message(ex.getMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefereshTokenException(InvalidRefreshTokenException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().status(HttpStatus.FORBIDDEN).message(ex.getMessage()).build(),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().status(HttpStatus.NOT_FOUND).message(ex.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().status(HttpStatus.FORBIDDEN).message(ex.getMessage()).build(),
        HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().status(HttpStatus.BAD_REQUEST).message(ex.getMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }
}
