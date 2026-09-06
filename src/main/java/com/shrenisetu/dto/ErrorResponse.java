package com.shrenisetu.dto;

import java.time.Instant;
import java.util.UUID;

public class ErrorResponse {
    private String code;
    private String message;
    private String timestamp;
    private String traceId;

    public ErrorResponse() {
        this.timestamp = Instant.now().toString();
        this.traceId = UUID.randomUUID().toString();
    }

    public ErrorResponse(String code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(String code, String message, String traceId) {
        this();
        this.code = code;
        this.message = message;
        if (traceId != null) this.traceId = traceId;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
}
