package com.shrenisetu.controller;

import com.shrenisetu.config.FirebaseConfig;
import com.shrenisetu.dto.HealthResponse;
import com.shrenisetu.service.FirestoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Public health and status endpoints")
public class HealthController {

    private final FirestoreService firestoreService;

    @Autowired
    public HealthController(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    @GetMapping("/health")
    @Operation(summary = "Check backend and Firebase connection health", description = "Public endpoint indicating backend readiness and Firebase connectivity.")
    public ResponseEntity<HealthResponse> getHealth() {
        boolean firestoreOk = firestoreService.isFirestoreConnected();
        HealthResponse response = new HealthResponse(
                "UP",
                "shreni-setu-backend",
                "1.0.0",
                true,
                firestoreOk
        );
        return ResponseEntity.ok(response);
    }
}
