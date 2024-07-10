package com.lucasmoraist.planner.exception;

import org.springframework.http.HttpStatus;

public record ExceptionDTO(String msg, HttpStatus status) {
}
