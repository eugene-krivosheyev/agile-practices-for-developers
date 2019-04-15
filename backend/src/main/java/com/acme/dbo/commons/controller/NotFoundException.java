package com.acme.dbo.commons.controller;

public class NotFoundException extends Exception {
    public NotFoundException() { }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
