package com.acme.dbo.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public void reportEntityNotFound(Exception e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
    }
}
