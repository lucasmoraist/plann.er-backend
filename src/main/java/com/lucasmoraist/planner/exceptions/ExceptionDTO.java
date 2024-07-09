package com.lucasmoraist.planner.exceptions;

import org.springframework.http.HttpStatus;

public record ExceptionDTO(String msg, HttpStatus status) {
}
