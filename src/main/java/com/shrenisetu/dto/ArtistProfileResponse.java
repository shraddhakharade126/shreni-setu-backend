package com.shrenisetu.dto;

import com.shrenisetu.model.ArtistProfile;
import com.shrenisetu.model.UserProfile;

public class ArtistProfileResponse {
    private String uid;
    private String fullName;
    private String phoneNumber;
    private String email;
    private boolean phoneVerified;
    private boolean emailVerified;
    private String craftCategory;
    private String state;
    private String district;
    private String village;
    private int experienceYears;
    private String preferredLanguage;
    private String bio;
    private String profilePhotoUrl;
    private String identityStatus;
    private String onboardingStep;
    private String overallStatus;
    private String createdAt;
    private String updatedAt;

    public ArtistProfileResponse() {}

    public static ArtistProfileResponse from(UserProfile user, ArtistProfile artist) {
        ArtistProfileResponse res = new ArtistProfileResponse();
        if (user != null) {
            res.setUid(user.getUid());
            res.setPhoneNumber(user.getPhoneNumber());
            res.setEmail(user.getEmail());
            res.setPhoneVerified(user.isPhoneVerified());
            res.setEmailVerified(user.isEmailVerified());
            res.setIdentityStatus(user.getIdentityStatus().name());
            res.setOnboardingStep(user.getOnboardingStep().name());
            res.setOverallStatus(user.getOverallStatus().name());
        }
        if (artist != null) {
            if (res.getUid() == null) res.setUid(artist.getUid());
            res.setFullName(artist.getFullName());
            res.setCraftCategory(artist.getCraftCategory());
            res.setState(artist.getState());
            res.setDistrict(artist.getDistrict());
            res.setVillage(artist.getVillage());
            res.setExperienceYears(artist.getExperienceYears());
            res.setPreferredLanguage(artist.getPreferredLanguage());
            res.setBio(artist.getBio());
            res.setProfilePhotoUrl(artist.getProfilePhotoUrl());
            res.setCreatedAt(artist.getCreatedAt());
            res.setUpdatedAt(artist.getUpdatedAt());
        }
        return res;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getCraftCategory() { return craftCategory; }
    public void setCraftCategory(String craftCategory) { this.craftCategory = craftCategory; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public String getIdentityStatus() { return identityStatus; }
    public void setIdentityStatus(String identityStatus) { this.identityStatus = identityStatus; }

    public String getOnboardingStep() { return onboardingStep; }
    public void setOnboardingStep(String onboardingStep) { this.onboardingStep = onboardingStep; }

    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
