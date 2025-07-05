package com.itss.ecommerce.dto.product;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.itss.ecommerce.dto.ProductDTO;

import jakarta.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DVDDTO extends ProductDTO {
    
    private Long dvdId;
    
    private String releaseDate;
    
    @Size(max = 50, message = "DVD type must not exceed 50 characters")
    private String dvdType; // DVD, Blu-ray, 4K Ultra HD, etc.
    
    @Size(max = 100, message = "Genre must not exceed 100 characters")
    private String genre;
    
    @Size(max = 200, message = "Studio must not exceed 200 characters")
    private String studio;
    
    @Size(max = 500, message = "Directors must not exceed 500 characters")
    private String directors;
    
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;
    
    @Size(max = 10, message = "Rating must not exceed 10 characters")
    private String rating; // G, PG, PG-13, R, etc.
    
    // Additional computed fields
    private String[] directorList;
    private Boolean isBluRay;
    private Boolean is4K;
    private String formattedDuration;
    private Boolean isChildFriendly;
    private Boolean isFeatureLength;
    
    /**
     * Get director list as array
     */
    public String[] getDirectorList() {
        if (directors == null || directors.trim().isEmpty()) {
            return new String[0];
        }
        return directors.split(",\\s*");
    }
    
    /**
     * Check if this is a Blu-ray disc
     */
    public Boolean getIsBluRay() {
        return dvdType != null && dvdType.toLowerCase().contains("blu");
    }
    
    /**
     * Check if this is 4K Ultra HD
     */
    public Boolean getIs4K() {
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
    public Boolean getIsChildFriendly() {
        return rating != null && (rating.equals("G") || rating.equals("PG"));
    }
    
    /**
     * Check if this is a feature-length film (over 75 minutes)
     */
    public Boolean getIsFeatureLength() {
        return durationMinutes != null && durationMinutes >= 75;
    }
    
    /**
     * Get primary director (first in the list)
     */
    public String getPrimaryDirector() {
        String[] directorArray = getDirectorList();
        return directorArray.length > 0 ? directorArray[0] : "Unknown Director";
    }
    
    /**
     * Get release year from release date
     */
    public String getReleaseYear() {
        if (releaseDate == null || releaseDate.length() < 4) {
            return "Unknown";
        }
        return releaseDate.substring(0, 4);
    }
    
    /**
     * Get video quality description
     */
    public String getVideoQuality() {
        if (getIs4K()) {
            return "4K Ultra HD";
        } else if (getIsBluRay()) {
            return "High Definition (Blu-ray)";
        } else {
            return "Standard Definition (DVD)";
        }
    }
    
    /**
     * Get content advisory based on rating
     */
    public String getContentAdvisory() {
        if (rating == null) return "Rating not specified";
        
        return switch (rating) {
            case "G" -> "General Audiences - All ages admitted";
            case "PG" -> "Parental Guidance - Some material may not be suitable for children";
            case "PG-13" -> "Parents Strongly Cautioned - Some material may be inappropriate for children under 13";
            case "R" -> "Restricted - Under 17 requires accompanying parent or adult guardian";
            case "NC-17" -> "Adults Only - No one 17 and under admitted";
            default -> "Rating: " + rating;
        };
    }
}