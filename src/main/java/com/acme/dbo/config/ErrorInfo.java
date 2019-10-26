package com.acme.dbo.config;

import java.time.LocalDateTime;

public class ErrorInfo {
    public final LocalDateTime timestamp;
    public final int errorCode;
    public final String message;

    public ErrorInfo(LocalDateTime timestamp, int errorCode, String message) {
        this.timestamp = timestamp;
        this.errorCode = errorCode;
        this.message = message;
    }
}
