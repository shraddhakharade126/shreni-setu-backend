package com.shrenisetu.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {

    private final String uid;
    private final String tokenString;
    private final Map<String, Object> claims;

    public FirebaseAuthenticationToken(String uid, String tokenString, Map<String, Object> claims, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.uid = uid;
        this.tokenString = tokenString;
        this.claims = claims;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return tokenString;
    }

    @Override
    public Object getPrincipal() {
        return uid;
    }

    public String getUid() {
        return uid;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    public String getPhoneNumber() {
        return claims != null ? (String) claims.get("phone_number") : null;
    }

    public String getEmail() {
        return claims != null ? (String) claims.get("email") : null;
    }

    public boolean isEmailVerified() {
        return claims != null && Boolean.TRUE.equals(claims.get("email_verified"));
    }
}
