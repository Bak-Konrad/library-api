package com.example.libraryapi.exception;

import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;

public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ValidationExceptionDto exDto = new ValidationExceptionDto();
        ex.getFieldErrors().forEach(fieldError ->
                exDto.addViolation(fieldError.getField(), fieldError.getDefaultMessage()));
        return new ResponseEntity<>(exDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(EntityNotFoundException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JdbcSQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleSQLIntegrityViolation(JdbcSQLIntegrityConstraintViolationException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDto> handleAccessDeniedException(AccessDeniedException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(ex.getMessage());
        return new ResponseEntity<>(exceptionDto, HttpStatus.FORBIDDEN);
    }
}
