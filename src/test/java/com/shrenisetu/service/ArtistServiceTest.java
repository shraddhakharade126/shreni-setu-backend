package com.shrenisetu.service;

import com.shrenisetu.dto.ArtistProfileResponse;
import com.shrenisetu.dto.CreateArtistProfileRequest;
import com.shrenisetu.dto.OnboardingStatusResponse;
import com.shrenisetu.dto.UpdateArtistProfileRequest;
import com.shrenisetu.exception.ResourceNotFoundException;
import com.shrenisetu.model.ArtistProfile;
import com.shrenisetu.model.UserProfile;
import com.shrenisetu.security.FirebaseAuthenticationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArtistServiceTest {

    private FirestoreService firestoreService;
    private AuditService auditService;
    private ArtistService artistService;

    @BeforeEach
    void setUp() {
        firestoreService = Mockito.mock(FirestoreService.class);
        auditService = Mockito.mock(AuditService.class);
        artistService = new ArtistService(firestoreService, auditService);
    }

    @Test
    void testGetProfileSuccess() {
        String uid = "test-uid-1";
        UserProfile user = new UserProfile(uid, "+919876543210", "test@shrenisetu.org", true, true);
        ArtistProfile artist = new ArtistProfile(uid, "Ramesh Kumar", "Madhubani Painting", "Bihar", "Madhubani", "Ranti", 15, "Hindi", "Master artisan");

        when(firestoreService.getUser(uid)).thenReturn(user);
        when(firestoreService.getArtist(uid)).thenReturn(artist);

        ArtistProfileResponse response = artistService.getProfile(uid);

        assertNotNull(response);
        assertEquals(uid, response.getUid());
        assertEquals("Ramesh Kumar", response.getFullName());
        assertEquals("+919876543210", response.getPhoneNumber());
        assertEquals("Madhubani Painting", response.getCraftCategory());
    }

    @Test
    void testGetProfileNotFoundThrowsException() {
        String uid = "unknown-uid";
        when(firestoreService.getUser(uid)).thenReturn(null);
        when(firestoreService.getArtist(uid)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> artistService.getProfile(uid));
    }

    @Test
    void testCreateOrUpdateProfile() {
        String uid = "artisan-uid-100";
        CreateArtistProfileRequest request = new CreateArtistProfileRequest(
                "Sita Devi", "Sujani Embroidery", "Bihar", "Muzaffarpur", "Bhikhanpur", 12, "Maithili", "Practicing traditional needlecraft"
        );

        FirebaseAuthenticationToken token = new FirebaseAuthenticationToken(
                uid,
                "dummy-jwt",
                Map.of("phone_number", "+919876543211", "email", "sita@shrenisetu.org", "email_verified", true),
                Collections.emptyList()
        );

        when(firestoreService.getUser(uid)).thenReturn(null);
        when(firestoreService.getArtist(uid)).thenReturn(null);

        ArtistProfileResponse response = artistService.createOrUpdateProfile(uid, request, token);

        assertNotNull(response);
        assertEquals("Sita Devi", response.getFullName());
        assertEquals("+919876543211", response.getPhoneNumber());

        ArgumentCaptor<UserProfile> userCaptor = ArgumentCaptor.forClass(UserProfile.class);
        verify(firestoreService, times(1)).saveUser(userCaptor.capture());
        assertEquals(uid, userCaptor.getValue().getUid());
        assertTrue(userCaptor.getValue().isPhoneVerified());

        ArgumentCaptor<ArtistProfile> artistCaptor = ArgumentCaptor.forClass(ArtistProfile.class);
        verify(firestoreService, times(1)).saveArtist(artistCaptor.capture());
        assertEquals("Sita Devi", artistCaptor.getValue().getFullName());

        verify(auditService, times(1)).logEvent(eq(uid), eq("ARTIST_PROFILE_CREATED"), any());
    }

    @Test
    void testPatchProfile() {
        String uid = "artisan-uid-200";
        ArtistProfile existingArtist = new ArtistProfile(uid, "Sita Devi", "Embroidery", "Bihar", "Muzaffarpur", "Village", 10, "Hindi", "Bio");
        UserProfile existingUser = new UserProfile(uid, "+919876543211", "sita@shrenisetu.org", true, true);

        when(firestoreService.getArtist(uid)).thenReturn(existingArtist);
        when(firestoreService.getUser(uid)).thenReturn(existingUser);

        UpdateArtistProfileRequest patch = new UpdateArtistProfileRequest();
        patch.setFullName("Sita Devi Shrestha");
        patch.setExperienceYears(14);

        ArtistProfileResponse response = artistService.patchProfile(uid, patch);

        assertNotNull(response);
        assertEquals("Sita Devi Shrestha", response.getFullName());
        assertEquals(14, response.getExperienceYears());
        verify(firestoreService, times(1)).saveArtist(any(ArtistProfile.class));
        verify(auditService, times(1)).logEvent(eq(uid), eq("ARTIST_PROFILE_PATCHED"), any());
    }

    @Test
    void testGetOnboardingStatus() {
        String uid = "artisan-uid-300";
        UserProfile user = new UserProfile(uid, "+919876543212", "artisan@shrenisetu.org", true, true);
        ArtistProfile artist = new ArtistProfile(uid, "Mohan Lal", "Terracotta", "Rajasthan", "Alwar", "Mollakka", 8, "Hindi", "Clay pottery");

        when(firestoreService.getUser(uid)).thenReturn(user);
        when(firestoreService.getArtist(uid)).thenReturn(artist);

        OnboardingStatusResponse status = artistService.getOnboardingStatus(uid);

        assertNotNull(status);
        assertTrue(status.isPhoneVerified());
        assertTrue(status.isProfileCompleted());
    }
}
