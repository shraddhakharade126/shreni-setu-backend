package com.shrenisetu.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuditLog {
    private String id;
    private String userId;
    private String eventType;
    private String timestamp;
    private Map<String, Object> details;

    public AuditLog() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now().toString();
        this.details = new HashMap<>();
    }

    public AuditLog(String userId, String eventType, Map<String, Object> details) {
        this();
        this.userId = userId;
        this.eventType = eventType;
        if (details != null) {
            this.details = details;
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("userId", userId);
        map.put("eventType", eventType);
        map.put("timestamp", timestamp);
        map.put("details", details);
        return map;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
