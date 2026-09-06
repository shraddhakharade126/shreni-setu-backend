package com.shrenisetu.controller;

import com.shrenisetu.dto.AuthVerificationResponse;
import com.shrenisetu.security.FirebaseAuthenticationToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Secure endpoints for verifying Firebase JWTs and retrieving authenticated identity")
@SecurityRequirement(name = "BearerAuth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/verify")
    @Operation(
            summary = "Verify Firebase ID Token and return authenticated Firebase UID",
            description = "Validates the Authorization: Bearer <ID_TOKEN> header via Firebase Admin SDK and returns the authenticated user's Firebase UID along with token claims."
    )
    public ResponseEntity<AuthVerificationResponse> verifyToken(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String uid = authentication.getName();
        log.info("Token verified successfully for Firebase UID: {}", uid);

        AuthVerificationResponse response = new AuthVerificationResponse();
        response.setUid(uid);

        if (authentication instanceof FirebaseAuthenticationToken firebaseToken) {
            response.setEmail(firebaseToken.getEmail());
            response.setEmailVerified(firebaseToken.isEmailVerified());
            response.setPhoneNumber(firebaseToken.getPhoneNumber());

            Map<String, Object> claims = firebaseToken.getClaims();
            if (claims != null) {
                if (claims.get("name") instanceof String name) {
                    response.setName(name);
                }
                if (claims.get("picture") instanceof String picture) {
                    response.setPicture(picture);
                }
                response.setClaims(claims);
            }
        }

        return ResponseEntity.ok(response);
    }
}
