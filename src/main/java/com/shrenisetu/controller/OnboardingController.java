package com.shrenisetu.controller;

import com.shrenisetu.dto.OnboardingStatusResponse;
import com.shrenisetu.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/onboarding")
@Tag(name = "Onboarding", description = "Onboarding status and state machine progression")
@SecurityRequirement(name = "BearerAuth")
public class OnboardingController {

    private static final Logger log = LoggerFactory.getLogger(OnboardingController.class);
    private final ArtistService artistService;

    @Autowired
    public OnboardingController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/status")
    @Operation(summary = "Get current onboarding status", description = "Returns real onboarding progression, verification status, and step for the authenticated artist.")
    public ResponseEntity<OnboardingStatusResponse> getOnboardingStatus(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String uid = principal.getName();
        log.debug("Fetching onboarding status for UID: {}", uid);
        OnboardingStatusResponse status = artistService.getOnboardingStatus(uid);
        return ResponseEntity.ok(status);
    }
}
