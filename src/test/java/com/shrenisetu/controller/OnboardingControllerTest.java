package com.shrenisetu.controller;

import com.shrenisetu.dto.OnboardingStatusResponse;
import com.shrenisetu.exception.GlobalExceptionHandler;
import com.shrenisetu.service.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OnboardingControllerTest {

    private MockMvc mockMvc;
    private ArtistService artistService;

    @BeforeEach
    void setUp() {
        artistService = Mockito.mock(ArtistService.class);
        OnboardingController controller = new OnboardingController(artistService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetOnboardingStatusSuccess() throws Exception {
        String uid = "uid-status-test";
        OnboardingStatusResponse status = new OnboardingStatusResponse();
        status.setUid(uid);
        status.setPhoneVerified(true);
        status.setIdentityStatus("NOT_STARTED");
        status.setOnboardingStep("BASIC_PROFILE_COMPLETED");
        status.setProfileCompleted(true);

        when(artistService.getOnboardingStatus(uid)).thenReturn(status);

        Principal principal = () -> uid;

        mockMvc.perform(get("/api/v1/onboarding/status")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value(uid))
                .andExpect(jsonPath("$.phoneVerified").value(true))
                .andExpect(jsonPath("$.identityStatus").value("NOT_STARTED"))
                .andExpect(jsonPath("$.profileCompleted").value(true));
    }
}
