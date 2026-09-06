package com.shrenisetu.controller;

import com.shrenisetu.dto.ArtistProfileResponse;
import com.shrenisetu.dto.CreateArtistProfileRequest;
import com.shrenisetu.dto.UpdateArtistProfileRequest;
import com.shrenisetu.security.FirebaseAuthenticationToken;
import com.shrenisetu.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/artists")
@Tag(name = "Artist Profile", description = "Endpoints for managing authenticated artist profiles")
@SecurityRequirement(name = "BearerAuth")
public class ArtistProfileController {

    private static final Logger log = LoggerFactory.getLogger(ArtistProfileController.class);
    private final ArtistService artistService;

    @Autowired
    public ArtistProfileController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated artist profile", description = "Retrieves the artist profile and user identity status for the authenticated Firebase user.")
    public ResponseEntity<ArtistProfileResponse> getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String uid = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Fetching artist profile for authenticated UID: {}", uid);

        ArtistProfileResponse profile = artistService.getProfile(uid);
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(profile);
    }

    @PostMapping("/profile")
    @Operation(summary = "Create or initialize artist profile", description = "Initializes basic artist profile and synchronizes verified phone/email from Firebase token into Firestore.")
    public ResponseEntity<ArtistProfileResponse> createProfile(
            @Valid @RequestBody CreateArtistProfileRequest request,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String uid = principal.getName();
        log.info("Creating/updating artist profile for UID: {}", uid);

        FirebaseAuthenticationToken token = null;
        if (principal instanceof FirebaseAuthenticationToken) {
            token = (FirebaseAuthenticationToken) principal;
        }

        ArtistProfileResponse profile = artistService.createOrUpdateProfile(uid, request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @PatchMapping("/profile")
    @Operation(summary = "Update artist profile fields", description = "Updates specified non-null fields of the authenticated artist profile.")
    public ResponseEntity<ArtistProfileResponse> updateProfile(
            @Valid @RequestBody UpdateArtistProfileRequest request,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String uid = principal.getName();
        log.info("Patching artist profile for UID: {}", uid);
        ArtistProfileResponse profile = artistService.patchProfile(uid, request);
        return ResponseEntity.ok(profile);
    }
}
