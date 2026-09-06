package com.shrenisetu.service;

import com.shrenisetu.model.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final FirestoreService firestoreService;

    @Autowired
    public AuditService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    public void logEvent(String userId, String eventType, Map<String, Object> details) {
        try {
            AuditLog auditLog = new AuditLog(userId, eventType, details);
            firestoreService.saveAuditLog(auditLog);
            log.info("Audit event recorded [{}]: user={}, details={}", eventType, userId, details);
        } catch (Exception e) {
            log.error("Failed to record audit event [{}]: {}", eventType, e.getMessage());
        }
    }
}
