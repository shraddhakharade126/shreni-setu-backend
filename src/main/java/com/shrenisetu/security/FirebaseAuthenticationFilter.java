package com.shrenisetu.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.shrenisetu.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(FirebaseAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final FirebaseTokenVerifier tokenVerifier;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FirebaseAuthenticationFilter(FirebaseTokenVerifier tokenVerifier) {
        this.tokenVerifier = tokenVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip auth filter for public endpoints
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || authHeader.isBlank()) {
            // No token provided; proceed to entry point if the endpoint requires authentication
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "MALFORMED_AUTHORIZATION_HEADER",
                    "Malformed Authorization header. Format must be 'Bearer <Firebase ID Token>'.");
            return;
        }

        String idToken = authHeader.substring(BEARER_PREFIX.length()).trim();

        if (idToken.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "EMPTY_TOKEN",
                    "Bearer token was empty. A valid Firebase ID token is required.");
            return;
        }

        if (tokenVerifier == null || !tokenVerifier.isAvailable()) {
            log.error("Firebase Auth is not initialized on server");
            sendErrorResponse(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "FIREBASE_AUTH_UNAVAILABLE",
                    "Firebase Authentication service is not configured on the server.");
            return;
        }

        try {
            // Verify the token and check for revocation
            VerifiedToken decodedToken = tokenVerifier.verifyIdToken(idToken, true);

            String uid = decodedToken.getUid();
            if (uid == null || uid.isBlank()) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN_UID",
                        "The Firebase token did not contain a valid user ID.");
                return;
            }

            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            FirebaseAuthenticationToken authentication = new FirebaseAuthenticationToken(
                    uid,
                    idToken,
                    decodedToken.getClaims(),
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Successfully authenticated Firebase UID: {}", uid);

            filterChain.doFilter(request, response);

        } catch (FirebaseAuthException e) {
            String errorCode = e.getAuthErrorCode() != null ? e.getAuthErrorCode().name() : "INVALID_FIREBASE_TOKEN";
            log.warn("Firebase ID token verification failed [{}]: {}", errorCode, e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, errorCode,
                    "Failed to verify Firebase ID token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Malformed token parameter: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "MALFORMED_TOKEN",
                    "The provided token is malformed.");
        } catch (Exception e) {
            log.error("Unexpected error verifying Firebase ID token", e);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_VERIFICATION_ERROR",
                    "Error verifying token: " + e.getMessage());
        }
    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("/api/v1/health") ||
               path.startsWith("/api/v1/actuator") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-ui") ||
               path.equals("/swagger-ui.html");
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse error = new ErrorResponse(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
