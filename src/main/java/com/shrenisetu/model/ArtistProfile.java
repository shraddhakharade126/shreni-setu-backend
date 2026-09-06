package com.shrenisetu.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ArtistProfile {
    private String uid;
    private String fullName;
    private String craftCategory;
    private String state;
    private String district;
    private String village;
    private int experienceYears;
    private String preferredLanguage;
    private String bio;
    private String profilePhotoUrl;
    private String createdAt;
    private String updatedAt;

    public ArtistProfile() {
        String now = Instant.now().toString();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public ArtistProfile(String uid, String fullName, String craftCategory, String state, String district, String village, int experienceYears, String preferredLanguage, String bio) {
        this();
        this.uid = uid;
        this.fullName = fullName;
        this.craftCategory = craftCategory;
        this.state = state;
        this.district = district;
        this.village = village;
        this.experienceYears = experienceYears;
        this.preferredLanguage = preferredLanguage;
        this.bio = bio;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("fullName", fullName);
        map.put("craftCategory", craftCategory);
        map.put("state", state);
        map.put("district", district);
        map.put("village", village);
        map.put("experienceYears", experienceYears);
        map.put("preferredLanguage", preferredLanguage);
        map.put("bio", bio);
        map.put("profilePhotoUrl", profilePhotoUrl);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        return map;
    }

    public static ArtistProfile fromMap(Map<String, Object> map) {
        if (map == null) return null;
        ArtistProfile p = new ArtistProfile();
        p.setUid((String) map.get("uid"));
        p.setFullName((String) map.get("fullName"));
        p.setCraftCategory((String) map.get("craftCategory"));
        p.setState((String) map.get("state"));
        p.setDistrict((String) map.get("district"));
        p.setVillage((String) map.get("village"));
        if (map.get("experienceYears") != null) {
            p.setExperienceYears(((Number) map.get("experienceYears")).intValue());
        }
        p.setPreferredLanguage((String) map.get("preferredLanguage"));
        p.setBio((String) map.get("bio"));
        p.setProfilePhotoUrl((String) map.get("profilePhotoUrl"));
        if (map.get("createdAt") != null) p.setCreatedAt((String) map.get("createdAt"));
        if (map.get("updatedAt") != null) p.setUpdatedAt((String) map.get("updatedAt"));
        return p;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

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

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
