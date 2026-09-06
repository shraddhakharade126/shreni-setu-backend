package com.shrenisetu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shrenisetu.dto.ArtistProfileResponse;
import com.shrenisetu.dto.CreateArtistProfileRequest;
import com.shrenisetu.dto.OnboardingStatusResponse;
import com.shrenisetu.exception.GlobalExceptionHandler;
import com.shrenisetu.exception.ResourceNotFoundException;
import com.shrenisetu.model.IdentityStatus;
import com.shrenisetu.model.OnboardingStep;
import com.shrenisetu.model.UserProfile;
import com.shrenisetu.security.FirebaseAuthenticationToken;
import com.shrenisetu.service.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ArtistProfileControllerTest {

    private MockMvc mockMvc;
    private ArtistService artistService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        artistService = Mockito.mock(ArtistService.class);
        ArtistProfileController controller = new ArtistProfileController(artistService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetCurrentProfileSuccess() throws Exception {
        String uid = "uid-12345";
        ArtistProfileResponse profile = new ArtistProfileResponse();
        profile.setUid(uid);
        profile.setFullName("Abdul Karim");
        profile.setCraftCategory("Bidriware");
        profile.setState("Karnataka");

        when(artistService.getProfile(uid)).thenReturn(profile);

        FirebaseAuthenticationToken auth = new FirebaseAuthenticationToken(
                uid, "token", Collections.emptyMap(), Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/v1/artists/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value(uid))
                .andExpect(jsonPath("$.fullName").value("Abdul Karim"))
                .andExpect(jsonPath("$.craftCategory").value("Bidriware"));
    }

    @Test
    void testGetCurrentProfileNotFound() throws Exception {
        String uid = "uid-not-found";
        when(artistService.getProfile(uid)).thenThrow(new ResourceNotFoundException("No artist profile found for user ID: " + uid));

        FirebaseAuthenticationToken auth = new FirebaseAuthenticationToken(
                uid, "token", Collections.emptyMap(), Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/v1/artists/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void testGetCurrentProfileUnauthorizedWhenNoAuthentication() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/v1/artists/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateProfileSuccess() throws Exception {
        String uid = "uid-67890";
        CreateArtistProfileRequest request = new CreateArtistProfileRequest(
                "Geeta Ben", "Bandhani Tie-Dye", "Gujarat", "Kutch", "Bhuj", 10, "Gujarati", "Master bandhani artist"
        );

        ArtistProfileResponse response = new ArtistProfileResponse();
        response.setUid(uid);
        response.setFullName("Geeta Ben");
        response.setCraftCategory("Bandhani Tie-Dye");

        when(artistService.createOrUpdateProfile(eq(uid), any(CreateArtistProfileRequest.class), any()))
                .thenReturn(response);

        FirebaseAuthenticationToken auth = new FirebaseAuthenticationToken(
                uid, "token", Collections.emptyMap(), Collections.emptyList()
        );

        mockMvc.perform(post("/api/v1/artists/profile")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uid").value(uid))
                .andExpect(jsonPath("$.fullName").value("Geeta Ben"));
    }

    @Test
    void testCreateProfileValidationFailure() throws Exception {
        String uid = "uid-67890";
        // Invalid request: fullName is blank
        CreateArtistProfileRequest invalidRequest = new CreateArtistProfileRequest(
                "", "Craft", "State", "District", "Village", 5, "Hindi", "Bio"
        );

        Principal principal = () -> uid;

        mockMvc.perform(post("/api/v1/artists/profile")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
