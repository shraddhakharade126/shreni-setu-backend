package com.shrenisetu.controller;

import com.shrenisetu.exception.GlobalExceptionHandler;
import com.shrenisetu.security.FirebaseAuthenticationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testVerifyTokenReturnsUidAndDetails() throws Exception {
        String uid = "firebase-user-abc-123";
        Map<String, Object> claims = Map.of(
                "phone_number", "+919876543210",
                "email", "artisan@shrenisetu.org",
                "email_verified", true,
                "name", "Ananya Sharma"
        );

        FirebaseAuthenticationToken authToken = new FirebaseAuthenticationToken(
                uid,
                "dummy-bearer-jwt",
                claims,
                Collections.emptyList()
        );

        mockMvc.perform(get("/api/v1/auth/verify")
                        .principal(authToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value(uid))
                .andExpect(jsonPath("$.phoneNumber").value("+919876543210"))
                .andExpect(jsonPath("$.email").value("artisan@shrenisetu.org"))
                .andExpect(jsonPath("$.emailVerified").value(true))
                .andExpect(jsonPath("$.name").value("Ananya Sharma"));
    }

    @Test
    void testVerifyTokenWithSimplePrincipalReturnsUid() throws Exception {
        String uid = "firebase-user-xyz-999";
        FirebaseAuthenticationToken auth = new FirebaseAuthenticationToken(
                uid, "token", Collections.emptyMap(), Collections.emptyList()
        );

        mockMvc.perform(get("/api/v1/auth/verify")
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value(uid));
    }

    @Test
    void testVerifyTokenWithoutPrincipalReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/auth/verify")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
