package com.itss.ecommerce.dto;

public class UserProfileDTO {
    private Long userId;
    private String email;
    private String name;
    private Boolean isActive;
    private String phone;
    private String role;

    public UserProfileDTO() {
    }

    public UserProfileDTO(Long userId, String email, String name, Boolean isActive, String phone, String role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.isActive = isActive;
        this.phone = phone;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}