package com.shrenisetu.security;

import java.util.Collections;
import java.util.Map;

public class VerifiedToken {
    private final String uid;
    private final Map<String, Object> claims;

    public VerifiedToken(String uid, Map<String, Object> claims) {
        this.uid = uid;
        this.claims = claims != null ? claims : Collections.emptyMap();
    }

    public String getUid() {
        return uid;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }
}
