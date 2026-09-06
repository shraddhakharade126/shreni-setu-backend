package com.shrenisetu.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.config.path:}")
    private String firebaseConfigPath;

    @Value("${firebase.project-id:shreni-setu-prod}")
    private String projectId;

    @Value("${firebase.emulator.enabled:false}")
    private boolean emulatorEnabled;

    @Value("${firebase.emulator.host:localhost:9099}")
    private String emulatorHost;

    @Value("${firestore.emulator.host:localhost:8080}")
    private String firestoreEmulatorHost;

    @Bean
    public FirebaseApp firebaseApp() {
        List<FirebaseApp> apps = FirebaseApp.getApps();
        if (!apps.isEmpty()) {
            return apps.get(0);
        }

        try {
            FirebaseOptions.Builder builder = FirebaseOptions.builder();
            GoogleCredentials credentials = resolveCredentials();

            if (credentials != null) {
                builder.setCredentials(credentials);
            }
            builder.setProjectId(projectId);

            if (emulatorEnabled) {
                log.info("Connecting to Firebase Auth Emulator at {}", emulatorHost);
                System.setProperty("FIREBASE_AUTH_EMULATOR_HOST", emulatorHost);
            }

            FirebaseApp app = FirebaseApp.initializeApp(builder.build());
            log.info("Initialized FirebaseApp successfully for project: {}", projectId);
            return app;
        } catch (Exception e) {
            log.warn("Firebase initialization warning: {}. Running with unconfigured Firebase credentials. Real token verification will require valid service account credentials.", e.getMessage());
            return null;
        }
    }

    private GoogleCredentials resolveCredentials() {
        if (firebaseConfigPath != null && !firebaseConfigPath.isBlank()) {
            File file = new File(firebaseConfigPath);
            if (file.exists()) {
                try (InputStream serviceAccount = new FileInputStream(file)) {
                    log.info("Loading Firebase credentials from file: {}", firebaseConfigPath);
                    return GoogleCredentials.fromStream(serviceAccount);
                } catch (IOException e) {
                    log.error("Failed to read credentials from {}: {}", firebaseConfigPath, e.getMessage());
                }
            } else {
                log.warn("Firebase config file not found at path: {}", firebaseConfigPath);
            }
        }

        try {
            log.info("Attempting to load Google Application Default Credentials...");
            return GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            log.warn("No Google Application Default Credentials found: {}", e.getMessage());
            return null;
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        if (firebaseApp == null) {
            log.warn("FirebaseAuth bean cannot be created because FirebaseApp is null.");
            return null;
        }
        return FirebaseAuth.getInstance(firebaseApp);
    }

    @Bean
    @ConditionalOnMissingBean
    public Firestore firestore(FirebaseApp firebaseApp) {
        try {
            FirestoreOptions.Builder builder = FirestoreOptions.newBuilder()
                    .setProjectId(projectId);

            if (emulatorEnabled || System.getenv("FIRESTORE_EMULATOR_HOST") != null) {
                String host = System.getenv("FIRESTORE_EMULATOR_HOST") != null ?
                        System.getenv("FIRESTORE_EMULATOR_HOST") : firestoreEmulatorHost;
                log.info("Configuring Firestore to use emulator at {}", host);
                builder.setHost(host);
                builder.setCredentials(com.google.cloud.NoCredentials.getInstance());
            } else {
                GoogleCredentials credentials = resolveCredentials();
                if (credentials != null) {
                    builder.setCredentials(credentials);
                }
            }

            return builder.build().getService();
        } catch (Exception e) {
            log.warn("Firestore client initialization failed: {}. Offline / in-memory fallback will be utilized if Firestore is unavailable.", e.getMessage());
            return null;
        }
    }
}
