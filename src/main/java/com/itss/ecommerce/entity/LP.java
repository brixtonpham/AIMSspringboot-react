package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "lp")
@DiscriminatorValue("lp")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "product_id")
public class LP extends Product {
    
    @Column(name = "artist")
    private String artist;
    
    @Column(name = "record_label")
    private String recordLabel;
    
    @Column(name = "music_type")
    private String musicType;
    
    @Column(name = "release_date")
    private String releaseDate;
    
    @Column(name = "tracklist", columnDefinition = "TEXT")
    private String tracklist;
    
    @Column(name = "rpm")
    private Integer rpm; // Records per minute (33, 45, 78)
    
    @Column(name = "size_inches")
    private Double sizeInches; // 7", 10", 12"
    
    @Column(name = "vinyl_condition")
    private String vinylCondition; // Mint, Near Mint, Very Good Plus, etc.
    
    @Column(name = "sleeve_condition")
    private String sleeveCondition;
    
    @Override
    public String getProductType() {
        return "lp";
    }
    
    /**
     * Get track list as array (split by comma)
     */
    public String[] getTrackArray() {
        if (tracklist == null || tracklist.trim().isEmpty()) {
            return new String[0];
        }
        return tracklist.split(",\\s*");
    }
    
    /**
     * Set track list from array
     */
    public void setTrackArray(String[] tracks) {
        if (tracks == null || tracks.length == 0) {
            this.tracklist = null;
        } else {
            this.tracklist = String.join(", ", tracks);
        }
    }
    
    /**
     * Get number of tracks
     */
    public int getTrackCount() {
        return getTrackArray().length;
    }
    
    /**
     * Check if this is a standard size LP (12 inches)
     */
    public boolean isStandardSize() {
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
     * Check if this is a vintage record (78 RPM)
     */
    public boolean isVintage() {
        return rpm != null && rpm == 78;
    }
    
    /**
     * Check if this is a single (45 RPM)
     */
    public boolean isSingle() {
        return rpm != null && rpm == 45;
    }
}