package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "dvds")
@DiscriminatorValue("dvd")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DVD extends Product {
    
    
    @Column(name = "release_date")
    private String releaseDate;
    
    @Column(name = "dvd_type", length = 50)
    private String dvdType; // DVD, Blu-ray, 4K Ultra HD, etc.
    
    @Column(name = "genre", length = 100)
    private String genre;
    
    @Column(name = "studio", length = 200)
    private String studio;
    
    @Column(name = "directors", length = 500)
    private String directors;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "rating", length = 10)
    private String rating; // G, PG, PG-13, R, etc.
    
    @Override
    public String getProductType() {
        return "dvd";
    }
    
    /**
     * Get director list as array (split by comma)
     */
    public String[] getDirectorList() {
        if (directors == null || directors.trim().isEmpty()) {
            return new String[0];
        }
        return directors.split(",\\s*");
    }
    
    /**
     * Set directors from array
     */
    public void setDirectorList(String[] directorList) {
        if (directorList == null || directorList.length == 0) {
            this.directors = null;
        } else {
            this.directors = String.join(", ", directorList);
        }
    }
    
    /**
     * Check if this is a Blu-ray disc
     */
    public boolean isBluRay() {
        return dvdType != null && dvdType.toLowerCase().contains("blu");
    }
    
    /**
     * Check if this is 4K Ultra HD
     */
    public boolean is4K() {
        return dvdType != null && dvdType.toLowerCase().contains("4k");
    }
    
    /**
     * Get formatted duration as hours and minutes
     */
    public String getFormattedDuration() {
        if (durationMinutes == null || durationMinutes <= 0) {
            return "Unknown";
        }
        
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
    
    /**
     * Check if suitable for children (G or PG rating)
     */
    public boolean isChildFriendly() {
        return rating != null && (rating.equals("G") || rating.equals("PG"));
    }
    
    /**
     * Check if this is a feature-length film (over 75 minutes)
     */
    public boolean isFeatureLength() {
        return durationMinutes != null && durationMinutes >= 75;
    }
}