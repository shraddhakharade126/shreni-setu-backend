package com.shrenisetu.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.shrenisetu.model.ArtistProfile;
import com.shrenisetu.model.AuditLog;
import com.shrenisetu.model.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class FirestoreService {

    private static final Logger log = LoggerFactory.getLogger(FirestoreService.class);

    private static final String USERS_COLLECTION = "users";
    private static final String ARTISTS_COLLECTION = "artists";
    private static final String AUDIT_COLLECTION = "auditLogs";

    private final Firestore firestore;

    // In-memory fallback cache when live Firestore is offline or credentials not yet provided
    private final Map<String, UserProfile> inMemoryUsers = new ConcurrentHashMap<>();
    private final Map<String, ArtistProfile> inMemoryArtists = new ConcurrentHashMap<>();
    private final Map<String, AuditLog> inMemoryAuditLogs = new ConcurrentHashMap<>();

    @Autowired
    public FirestoreService(@Autowired(required = false) Firestore firestore) {
        this.firestore = firestore;
        if (firestore != null) {
            log.info("FirestoreService connected to Cloud Firestore.");
        } else {
            log.warn("FirestoreService running with in-memory persistence fallback. Real Cloud Firestore requires credentials.");
        }
    }

    public boolean isFirestoreConnected() {
        return firestore != null;
    }

    public UserProfile getUser(String uid) {
        if (uid == null || uid.isBlank()) return null;

        if (firestore != null) {
            try {
                DocumentReference docRef = firestore.collection(USERS_COLLECTION).document(uid);
                ApiFuture<DocumentSnapshot> future = docRef.get();
                DocumentSnapshot document = future.get(5, TimeUnit.SECONDS);
                if (document.exists()) {
                    return UserProfile.fromMap(document.getData());
                }
            } catch (Exception e) {
                log.error("Failed to fetch user from Firestore [{}]: {}", uid, e.getMessage());
            }
        }
        return inMemoryUsers.get(uid);
    }

    public void saveUser(UserProfile user) {
        if (user == null || user.getUid() == null) return;
        inMemoryUsers.put(user.getUid(), user);

        if (firestore != null) {
            try {
                DocumentReference docRef = firestore.collection(USERS_COLLECTION).document(user.getUid());
                ApiFuture<WriteResult> future = docRef.set(user.toMap(), SetOptions.merge());
                future.get(5, TimeUnit.SECONDS);
                log.debug("Saved user to Firestore: {}", user.getUid());
            } catch (Exception e) {
                log.error("Failed to save user to Firestore [{}]: {}", user.getUid(), e.getMessage());
            }
        }
    }

    public ArtistProfile getArtist(String uid) {
        if (uid == null || uid.isBlank()) return null;

        if (firestore != null) {
            try {
                DocumentReference docRef = firestore.collection(ARTISTS_COLLECTION).document(uid);
                ApiFuture<DocumentSnapshot> future = docRef.get();
                DocumentSnapshot document = future.get(5, TimeUnit.SECONDS);
                if (document.exists()) {
                    return ArtistProfile.fromMap(document.getData());
                }
            } catch (Exception e) {
                log.error("Failed to fetch artist from Firestore [{}]: {}", uid, e.getMessage());
            }
        }
        return inMemoryArtists.get(uid);
    }

    public void saveArtist(ArtistProfile artist) {
        if (artist == null || artist.getUid() == null) return;
        inMemoryArtists.put(artist.getUid(), artist);

        if (firestore != null) {
            try {
                DocumentReference docRef = firestore.collection(ARTISTS_COLLECTION).document(artist.getUid());
                ApiFuture<WriteResult> future = docRef.set(artist.toMap(), SetOptions.merge());
                future.get(5, TimeUnit.SECONDS);
                log.debug("Saved artist to Firestore: {}", artist.getUid());
            } catch (Exception e) {
                log.error("Failed to save artist to Firestore [{}]: {}", artist.getUid(), e.getMessage());
            }
        }
    }

    public void saveAuditLog(AuditLog auditLog) {
        if (auditLog == null) return;
        inMemoryAuditLogs.put(auditLog.getId(), auditLog);

        if (firestore != null) {
            try {
                DocumentReference docRef = firestore.collection(AUDIT_COLLECTION).document(auditLog.getId());
                docRef.set(auditLog.toMap());
            } catch (Exception e) {
                log.error("Failed to save audit log to Firestore [{}]: {}", auditLog.getId(), e.getMessage());
            }
        }
    }
}
