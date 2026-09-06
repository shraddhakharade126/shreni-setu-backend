package com.shrenisetu.dto;

import com.shrenisetu.model.UserProfile;

public class OnboardingStatusResponse {
    private String uid;
    private boolean phoneVerified;
    private boolean emailVerified;
    private String identityStatus;
    private String craftStatus;
    private String portfolioStatus;
    private String processStatus;
    private String overallStatus;
    private String onboardingStep;
    private boolean profileCompleted;
    private String updatedAt;

    public OnboardingStatusResponse() {}

    public static OnboardingStatusResponse from(UserProfile user, boolean profileCompleted) {
        OnboardingStatusResponse res = new OnboardingStatusResponse();
        if (user != null) {
            res.setUid(user.getUid());
            res.setPhoneVerified(user.isPhoneVerified());
            res.setEmailVerified(user.isEmailVerified());
            res.setIdentityStatus(user.getIdentityStatus().name());
            res.setCraftStatus(user.getCraftStatus().name());
            res.setPortfolioStatus(user.getPortfolioStatus().name());
            res.setProcessStatus(user.getProcessStatus().name());
            res.setOverallStatus(user.getOverallStatus().name());
            res.setOnboardingStep(user.getOnboardingStep().name());
            res.setProfileCompleted(profileCompleted);
            res.setUpdatedAt(user.getUpdatedAt());
        }
        return res;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public boolean isPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getIdentityStatus() { return identityStatus; }
    public void setIdentityStatus(String identityStatus) { this.identityStatus = identityStatus; }

    public String getCraftStatus() { return craftStatus; }
    public void setCraftStatus(String craftStatus) { this.craftStatus = craftStatus; }

    public String getPortfolioStatus() { return portfolioStatus; }
    public void setPortfolioStatus(String portfolioStatus) { this.portfolioStatus = portfolioStatus; }

    public String getProcessStatus() { return processStatus; }
    public void setProcessStatus(String processStatus) { this.processStatus = processStatus; }

    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }

    public String getOnboardingStep() { return onboardingStep; }
    public void setOnboardingStep(String onboardingStep) { this.onboardingStep = onboardingStep; }

    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
