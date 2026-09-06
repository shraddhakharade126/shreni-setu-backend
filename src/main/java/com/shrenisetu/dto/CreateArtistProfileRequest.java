package com.shrenisetu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateArtistProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    private String craftCategory;
    private String state;
    private String district;
    private String village;
    private Integer experienceYears;
    private String preferredLanguage;
    private String bio;

    public CreateArtistProfileRequest() {}

    public CreateArtistProfileRequest(String fullName, String craftCategory, String state, String district, String village, Integer experienceYears, String preferredLanguage, String bio) {
        this.fullName = fullName;
        this.craftCategory = craftCategory;
        this.state = state;
        this.district = district;
        this.village = village;
        this.experienceYears = experienceYears;
        this.preferredLanguage = preferredLanguage;
        this.bio = bio;
    }

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

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
