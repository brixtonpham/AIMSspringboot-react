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
public class CDDTO extends ProductDTO {
    
    private Long cdId;
    
    @Size(max = 2000, message = "Track list must not exceed 2000 characters")
    private String trackList;
    
    @Size(max = 100, message = "Genre must not exceed 100 characters")
    private String genre;
    
    @Size(max = 200, message = "Record label must not exceed 200 characters")
    private String recordLabel;
    
    @Size(max = 500, message = "Artists must not exceed 500 characters")
    private String artists;
    
    private String releaseDate;
    
    // Additional computed fields
    private String[] trackArray;
    private String[] artistList;
    private Integer trackCount;
    private Boolean isCompilation;
    
    /**
     * Get track list as array
     */
    public String[] getTrackArray() {
        if (trackList == null || trackList.trim().isEmpty()) {
            return new String[0];
        }
        return trackList.split(",\\s*");
    }
    
    /**
     * Get artist list as array
     */
    public String[] getArtistList() {
        if (artists == null || artists.trim().isEmpty()) {
            return new String[0];
        }
        return artists.split(",\\s*");
    }
    
    /**
     * Get number of tracks
     */
    public Integer getTrackCount() {
        return getTrackArray().length;
    }
    
    /**
     * Check if this is a compilation album
     */
    public Boolean getIsCompilation() {
        return getArtistList().length > 3; // More than 3 artists suggests compilation
    }
    
    /**
     * Get primary artist (first in the list)
     */
    public String getPrimaryArtist() {
        String[] artistArray = getArtistList();
        return artistArray.length > 0 ? artistArray[0] : "Unknown Artist";
    }
    
    /**
     * Get formatted track count
     */
    public String getFormattedTrackCount() {
        int count = getTrackCount();
        return count + " track" + (count != 1 ? "s" : "");
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
}