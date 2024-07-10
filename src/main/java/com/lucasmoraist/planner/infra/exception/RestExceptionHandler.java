package com.lucasmoraist.planner.infra.exception;

import com.lucasmoraist.planner.exception.ExceptionDTO;
import com.lucasmoraist.planner.exception.ResourceNotFound;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFound.class)
    protected ResponseEntity<ExceptionDTO> handleResourceNotFound(ResourceNotFound e) {
        return ResponseEntity.badRequest().body(new ExceptionDTO(e.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ExceptionDTO> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ExceptionDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionDTO> handleException(Exception e) {
        return ResponseEntity.badRequest().body(new ExceptionDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ExceptionDTO> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body(new ExceptionDTO(e.getMessage(), HttpStatus.BAD_REQUEST));
    }

}
