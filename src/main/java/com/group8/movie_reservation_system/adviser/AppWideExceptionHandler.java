package com.group8.movie_reservation_system.adviser;


import com.group8.movie_reservation_system.exception.*;
import com.group8.movie_reservation_system.util.StandardResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppWideExceptionHandler {
    @ExceptionHandler(EntryNotFoundException.class)
    public ResponseEntity<StandardResponseDto> handleEntryNotFoundException(EntryNotFoundException ex) {
        return new ResponseEntity<StandardResponseDto>(
                new StandardResponseDto(404,ex.getMessage(),ex),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<StandardResponseDto> handleUnAuthorizedException(UnAuthorizedException ex) {
        return new ResponseEntity<StandardResponseDto>(
                new StandardResponseDto(401,ex.getMessage(),ex),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<StandardResponseDto> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<StandardResponseDto>(
                new StandardResponseDto(400,ex.getMessage(),ex),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<StandardResponseDto> handleDuplicateException(DuplicateEntryException ex) {
        return new ResponseEntity<StandardResponseDto>(
                new StandardResponseDto(409,ex.getMessage(),ex),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardResponseDto> handleBadCredentialException(BadCredentialsException ex) {
        return new ResponseEntity<StandardResponseDto>(
                new StandardResponseDto(401,ex.getMessage(),ex),
                HttpStatus.UNAUTHORIZED
        );
    }
}