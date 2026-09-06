package com.shrenisetu.security;

import com.google.firebase.auth.FirebaseAuthException;

public interface FirebaseTokenVerifier {
    VerifiedToken verifyIdToken(String idToken, boolean checkRevoked) throws FirebaseAuthException;
    boolean isAvailable();
}

