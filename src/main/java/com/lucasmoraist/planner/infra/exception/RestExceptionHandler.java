package com.lucasmoraist.planner.infra.exception;

import com.lucasmoraist.planner.exception.DatesInconsistency;
import com.lucasmoraist.planner.exception.ExceptionDTO;
import com.lucasmoraist.planner.exception.ResourceNotFound;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.DateTimeException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFound.class)
    protected ResponseEntity<ExceptionDTO> handleResourceNotFound(ResourceNotFound e) {
        return ResponseEntity.badRequest().body(new ExceptionDTO(e.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(DatesInconsistency.class)
    protected ResponseEntity<ExceptionDTO> handleDatesInconsistency(DatesInconsistency e) {
        return ResponseEntity.badRequest().body(new ExceptionDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ExceptionDTO> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body(new ExceptionDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(DateTimeException.class)
    protected ResponseEntity<ExceptionDTO> handleDateTimeException(DateTimeException e) {
        return ResponseEntity.badRequest().body(new ExceptionDTO("DateTime written incorrectly", HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ExceptionDTO> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.badRequest().body(new ExceptionDTO("Mandatory values not filled", HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ExceptionDTO> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ExceptionDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionDTO> handleException(Exception e) {
        return ResponseEntity.badRequest().body(new ExceptionDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
