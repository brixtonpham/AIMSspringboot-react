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
public class LPDTO extends ProductDTO {
    
    private Long lpId;
    
    @Size(max = 200, message = "Artist must not exceed 200 characters")
    private String artist;
    
    @Size(max = 200, message = "Record label must not exceed 200 characters")
    private String recordLabel;
    
    @Size(max = 100, message = "Music type must not exceed 100 characters")
    private String musicType;
    
    private String releaseDate;
    
    @Size(max = 2000, message = "Tracklist must not exceed 2000 characters")
    private String tracklist;
    
    @Positive(message = "RPM must be positive")
    private Integer rpm;
    
    @Positive(message = "Size must be positive")
    private Double sizeInches;
    
    @Size(max = 50, message = "Vinyl condition must not exceed 50 characters")
    private String vinylCondition;
    
    @Size(max = 50, message = "Sleeve condition must not exceed 50 characters")
    private String sleeveCondition;
    
    
    /**
     * Get track list as array
     */
    public String[] getTrackArray() {
        if (tracklist == null || tracklist.trim().isEmpty()) {
            return new String[0];
        }
        return tracklist.split(",\\s*");
    }
    
    /**
     * Get number of tracks
     */
    public Integer getTrackCount() {
        return getTrackArray().length;
    }
    
    /**
     * Check if this is a standard size LP (12 inches)
     */
    public Boolean getIsStandardSize() {
        return sizeInches != null && sizeInches == 12.0;
    }
    
    /**
     * Get formatted size with units
     */
    public String getFormattedSize() {
        if (sizeInches == null) {
            return "Unknown";
        }
        return String.format("%.1f\"", sizeInches);
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
     * Get formatted track count
     */
    public String getFormattedTrackCount() {
        int count = getTrackCount();
        return count + " track" + (count != 1 ? "s" : "");
    }
    
    /**
     * Get RPM description
     */
    public String getRPMDescription() {
        if (rpm == null) return "Unknown RPM";
        
        return switch (rpm) {
            case 33 -> "33⅓ RPM (LP)";
            case 45 -> "45 RPM (Single)";
            case 78 -> "78 RPM (Vintage)";
            default -> rpm + " RPM";
        };
    }
    
    /**
     * Get vinyl condition grade
     */
    public String getVinylConditionGrade() {
        if (vinylCondition == null) return "Not specified";
        
        String condition = vinylCondition.toLowerCase();
        if (condition.contains("mint")) return "M";
        if (condition.contains("near mint")) return "NM";
        if (condition.contains("very good plus")) return "VG+";
        if (condition.contains("very good")) return "VG";
        if (condition.contains("good plus")) return "G+";
        if (condition.contains("good")) return "G";
        if (condition.contains("fair")) return "F";
        if (condition.contains("poor")) return "P";
        return vinylCondition;
    }
}