package com.shrenisetu.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class UserProfile {
    private String uid;
    private String phoneNumber;
    private String email;
    private boolean phoneVerified;
    private boolean emailVerified;
    private IdentityStatus identityStatus = IdentityStatus.NOT_STARTED;
    private VerificationStatuses.CraftStatus craftStatus = VerificationStatuses.CraftStatus.NOT_STARTED;
    private VerificationStatuses.PortfolioStatus portfolioStatus = VerificationStatuses.PortfolioStatus.NOT_STARTED;
    private VerificationStatuses.ProcessStatus processStatus = VerificationStatuses.ProcessStatus.NOT_STARTED;
    private VerificationStatuses.OverallStatus overallStatus = VerificationStatuses.OverallStatus.IN_PROGRESS;
    private OnboardingStep onboardingStep = OnboardingStep.PHONE_OR_EMAIL_VERIFIED;
    private String createdAt;
    private String updatedAt;

    public UserProfile() {
        String now = Instant.now().toString();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UserProfile(String uid, String phoneNumber, String email, boolean phoneVerified, boolean emailVerified) {
        this();
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.phoneVerified = phoneVerified;
        this.emailVerified = emailVerified;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("phoneNumber", phoneNumber);
        map.put("email", email);
        map.put("phoneVerified", phoneVerified);
        map.put("emailVerified", emailVerified);
        map.put("identityStatus", identityStatus.name());
        map.put("craftStatus", craftStatus.name());
        map.put("portfolioStatus", portfolioStatus.name());
        map.put("processStatus", processStatus.name());
        map.put("overallStatus", overallStatus.name());
        map.put("onboardingStep", onboardingStep.name());
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        return map;
    }

    public static UserProfile fromMap(Map<String, Object> map) {
        if (map == null) return null;
        UserProfile p = new UserProfile();
        p.setUid((String) map.get("uid"));
        p.setPhoneNumber((String) map.get("phoneNumber"));
        p.setEmail((String) map.get("email"));
        p.setPhoneVerified(Boolean.TRUE.equals(map.get("phoneVerified")));
        p.setEmailVerified(Boolean.TRUE.equals(map.get("emailVerified")));

        if (map.get("identityStatus") != null) {
            try {
                p.setIdentityStatus(IdentityStatus.valueOf((String) map.get("identityStatus")));
            } catch (Exception ignored) {}
        }
        if (map.get("craftStatus") != null) {
            try {
                p.setCraftStatus(VerificationStatuses.CraftStatus.valueOf((String) map.get("craftStatus")));
            } catch (Exception ignored) {}
        }
        if (map.get("portfolioStatus") != null) {
            try {
                p.setPortfolioStatus(VerificationStatuses.PortfolioStatus.valueOf((String) map.get("portfolioStatus")));
            } catch (Exception ignored) {}
        }
        if (map.get("processStatus") != null) {
            try {
                p.setProcessStatus(VerificationStatuses.ProcessStatus.valueOf((String) map.get("processStatus")));
            } catch (Exception ignored) {}
        }
        if (map.get("overallStatus") != null) {
            try {
                p.setOverallStatus(VerificationStatuses.OverallStatus.valueOf((String) map.get("overallStatus")));
            } catch (Exception ignored) {}
        }
        if (map.get("onboardingStep") != null) {
            try {
                p.setOnboardingStep(OnboardingStep.valueOf((String) map.get("onboardingStep")));
            } catch (Exception ignored) {}
        }
        if (map.get("createdAt") != null) p.setCreatedAt((String) map.get("createdAt"));
        if (map.get("updatedAt") != null) p.setUpdatedAt((String) map.get("updatedAt"));
        return p;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public IdentityStatus getIdentityStatus() { return identityStatus; }
    public void setIdentityStatus(IdentityStatus identityStatus) { this.identityStatus = identityStatus; }

    public VerificationStatuses.CraftStatus getCraftStatus() { return craftStatus; }
    public void setCraftStatus(VerificationStatuses.CraftStatus craftStatus) { this.craftStatus = craftStatus; }

    public VerificationStatuses.PortfolioStatus getPortfolioStatus() { return portfolioStatus; }
    public void setPortfolioStatus(VerificationStatuses.PortfolioStatus portfolioStatus) { this.portfolioStatus = portfolioStatus; }

    public VerificationStatuses.ProcessStatus getProcessStatus() { return processStatus; }
    public void setProcessStatus(VerificationStatuses.ProcessStatus processStatus) { this.processStatus = processStatus; }

    public VerificationStatuses.OverallStatus getOverallStatus() { return overallStatus; }
    public void setOverallStatus(VerificationStatuses.OverallStatus overallStatus) { this.overallStatus = overallStatus; }

    public OnboardingStep getOnboardingStep() { return onboardingStep; }
    public void setOnboardingStep(OnboardingStep onboardingStep) { this.onboardingStep = onboardingStep; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
