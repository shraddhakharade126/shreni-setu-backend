package com.shrenisetu.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultFirebaseTokenVerifier implements FirebaseTokenVerifier {

    private static final Logger log = LoggerFactory.getLogger(DefaultFirebaseTokenVerifier.class);
    private final FirebaseAuth firebaseAuth;

    @Autowired
    public DefaultFirebaseTokenVerifier(@Autowired(required = false) FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public VerifiedToken verifyIdToken(String idToken, boolean checkRevoked) throws FirebaseAuthException {
        if (firebaseAuth == null) {
            log.error("FirebaseAuth is not configured on server.");
            throw new IllegalStateException("Firebase Authentication service is uninitialized. Real token verification requires valid credentials.");
        }
        FirebaseToken token = firebaseAuth.verifyIdToken(idToken, checkRevoked);
        return new VerifiedToken(token.getUid(), token.getClaims());
    }

    @Override
    public boolean isAvailable() {
        return firebaseAuth != null;
    }
}
