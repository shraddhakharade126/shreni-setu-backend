package com.shrenisetu.security;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FirebaseAuthenticationFilterTest {

    private FirebaseTokenVerifier tokenVerifier;
    private FirebaseAuthenticationFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        tokenVerifier = Mockito.mock(FirebaseTokenVerifier.class);
        when(tokenVerifier.isAvailable()).thenReturn(true);
        filter = new FirebaseAuthenticationFilter(tokenVerifier);
        filterChain = Mockito.mock(FilterChain.class);
    }

    @Test
    void testPublicEndpointBypassesFilter() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testMissingAuthorizationHeaderProceedsToFilterChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/artists/me");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        // Filter proceeds without setting authentication; downstream Spring Security will enforce 401
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testMalformedAuthorizationHeaderReturns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/artists/me");
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("MALFORMED_AUTHORIZATION_HEADER"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testEmptyBearerTokenReturns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/artists/me");
        request.addHeader("Authorization", "Bearer   ");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("EMPTY_TOKEN"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testInvalidTokenThrowsFirebaseAuthExceptionReturns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/artists/me");
        request.addHeader("Authorization", "Bearer invalid-token-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FirebaseAuthException exception = Mockito.mock(FirebaseAuthException.class);
        when(exception.getMessage()).thenReturn("Firebase ID token has expired or is invalid");
        when(tokenVerifier.verifyIdToken(eq("invalid-token-123"), anyBoolean())).thenThrow(exception);

        filter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Firebase ID token has expired or is invalid"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testValidFirebaseIdTokenAuthenticatesSuccessfully() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/artists/me");
        request.addHeader("Authorization", "Bearer valid-firebase-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        VerifiedToken decodedToken = new VerifiedToken(
                "artist-firebase-uid-789",
                Map.of(
                        "phone_number", "+919876543210",
                        "email", "artisan@shrenisetu.org",
                        "email_verified", true
                )
        );

        when(tokenVerifier.verifyIdToken("valid-firebase-token", true)).thenReturn(decodedToken);

        filter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("artist-firebase-uid-789", SecurityContextHolder.getContext().getAuthentication().getName());

        FirebaseAuthenticationToken authToken = (FirebaseAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertEquals("+919876543210", authToken.getPhoneNumber());
        assertEquals("artisan@shrenisetu.org", authToken.getEmail());
    }
}
