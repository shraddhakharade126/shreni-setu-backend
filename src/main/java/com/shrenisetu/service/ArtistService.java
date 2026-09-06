package com.shrenisetu.service;

import com.shrenisetu.dto.ArtistProfileResponse;
import com.shrenisetu.dto.CreateArtistProfileRequest;
import com.shrenisetu.dto.OnboardingStatusResponse;
import com.shrenisetu.dto.UpdateArtistProfileRequest;
import com.shrenisetu.exception.ResourceNotFoundException;
import com.shrenisetu.model.ArtistProfile;
import com.shrenisetu.model.OnboardingStep;
import com.shrenisetu.model.UserProfile;
import com.shrenisetu.security.FirebaseAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class ArtistService {

    private static final Logger log = LoggerFactory.getLogger(ArtistService.class);

    private final FirestoreService firestoreService;
    private final AuditService auditService;

    @Autowired
    public ArtistService(FirestoreService firestoreService, AuditService auditService) {
        this.firestoreService = firestoreService;
        this.auditService = auditService;
    }

    public ArtistProfileResponse getProfile(String uid) {
        UserProfile user = firestoreService.getUser(uid);
        ArtistProfile artist = firestoreService.getArtist(uid);

        if (user == null && artist == null) {
            throw new ResourceNotFoundException("No artist profile found for user ID: " + uid);
        }

        return ArtistProfileResponse.from(user, artist);
    }

    public ArtistProfileResponse createOrUpdateProfile(String uid, CreateArtistProfileRequest request, FirebaseAuthenticationToken token) {
        String now = Instant.now().toString();

        // 1. Sync User Profile
        UserProfile user = firestoreService.getUser(uid);
        boolean isNewUser = (user == null);
        if (isNewUser) {
            user = new UserProfile();
            user.setUid(uid);
            user.setCreatedAt(now);
        }

        if (token != null) {
            if (token.getPhoneNumber() != null && !token.getPhoneNumber().isBlank()) {
                user.setPhoneNumber(token.getPhoneNumber());
                user.setPhoneVerified(true);
            }
            if (token.getEmail() != null && !token.getEmail().isBlank()) {
                user.setEmail(token.getEmail());
                user.setEmailVerified(token.isEmailVerified());
            }
        }

        user.setOnboardingStep(OnboardingStep.BASIC_PROFILE_COMPLETED);
        user.setUpdatedAt(now);
        firestoreService.saveUser(user);

        // 2. Sync Artist Profile
        ArtistProfile artist = firestoreService.getArtist(uid);
        boolean isNewArtist = (artist == null);
        if (isNewArtist) {
            artist = new ArtistProfile();
            artist.setUid(uid);
            artist.setCreatedAt(now);
        }

        artist.setFullName(request.getFullName());
        artist.setCraftCategory(request.getCraftCategory());
        artist.setState(request.getState());
        artist.setDistrict(request.getDistrict());
        artist.setVillage(request.getVillage());
        if (request.getExperienceYears() != null) {
            artist.setExperienceYears(request.getExperienceYears());
        }
        artist.setPreferredLanguage(request.getPreferredLanguage());
        artist.setBio(request.getBio());
        artist.setUpdatedAt(now);
        firestoreService.saveArtist(artist);

        // 3. Audit Log
        String eventType = isNewArtist ? "ARTIST_PROFILE_CREATED" : "ARTIST_PROFILE_UPDATED";
        auditService.logEvent(uid, eventType, Map.of(
                "fullName", request.getFullName(),
                "craftCategory", request.getCraftCategory() != null ? request.getCraftCategory() : "",
                "state", request.getState() != null ? request.getState() : ""
        ));

        return ArtistProfileResponse.from(user, artist);
    }

    public ArtistProfileResponse patchProfile(String uid, UpdateArtistProfileRequest request) {
        String now = Instant.now().toString();

        ArtistProfile artist = firestoreService.getArtist(uid);
        UserProfile user = firestoreService.getUser(uid);

        if (artist == null && user == null) {
            throw new ResourceNotFoundException("Cannot update non-existent profile for user ID: " + uid);
        }

        if (artist == null) {
            artist = new ArtistProfile();
            artist.setUid(uid);
            artist.setCreatedAt(now);
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            artist.setFullName(request.getFullName());
        }
        if (request.getCraftCategory() != null) {
            artist.setCraftCategory(request.getCraftCategory());
        }
        if (request.getState() != null) {
            artist.setState(request.getState());
        }
        if (request.getDistrict() != null) {
            artist.setDistrict(request.getDistrict());
        }
        if (request.getVillage() != null) {
            artist.setVillage(request.getVillage());
        }
        if (request.getExperienceYears() != null) {
            artist.setExperienceYears(request.getExperienceYears());
        }
        if (request.getPreferredLanguage() != null) {
            artist.setPreferredLanguage(request.getPreferredLanguage());
        }
        if (request.getBio() != null) {
            artist.setBio(request.getBio());
        }
        if (request.getProfilePhotoUrl() != null) {
            artist.setProfilePhotoUrl(request.getProfilePhotoUrl());
        }
        artist.setUpdatedAt(now);
        firestoreService.saveArtist(artist);

        auditService.logEvent(uid, "ARTIST_PROFILE_PATCHED", Map.of("updatedAt", now));

        return ArtistProfileResponse.from(user, artist);
    }

    public OnboardingStatusResponse getOnboardingStatus(String uid) {
        UserProfile user = firestoreService.getUser(uid);
        ArtistProfile artist = firestoreService.getArtist(uid);

        if (user == null) {
            // First time accessing status: initialize user profile
            user = new UserProfile();
            user.setUid(uid);
            firestoreService.saveUser(user);
        }

        boolean profileCompleted = artist != null &&
                artist.getFullName() != null &&
                !artist.getFullName().isBlank();

        return OnboardingStatusResponse.from(user, profileCompleted);
    }
}
