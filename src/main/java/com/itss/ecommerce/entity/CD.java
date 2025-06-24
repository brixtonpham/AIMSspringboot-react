package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cds")
@DiscriminatorValue("cd")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CD extends Product {
    
    
    @Column(name = "track_list", columnDefinition = "TEXT")
    private String trackList;
    
    @Column(name = "genre", length = 100)
    private String genre;
    
    @Column(name = "record_label", length = 200)
    private String recordLabel;
    
    @Column(name = "artists", length = 500)
    private String artists;
    
    @Column(name = "release_date")
    private String releaseDate;
    
    @Override
    public String getProductType() {
        return "cd";
    }
    
    /**
     * Get track list as array (split by comma)
     */
    public String[] getTrackArray() {
        if (trackList == null || trackList.trim().isEmpty()) {
            return new String[0];
        }
        return trackList.split(",\\s*");
    }
    
    /**
     * Set track list from array
     */
    public void setTrackArray(String[] tracks) {
        if (tracks == null || tracks.length == 0) {
            this.trackList = null;
        } else {
            this.trackList = String.join(", ", tracks);
        }
    }
    
    /**
     * Get artist list as array (split by comma)
     */
    public String[] getArtistList() {
        if (artists == null || artists.trim().isEmpty()) {
            return new String[0];
        }
        return artists.split(",\\s*");
    }
    
    /**
     * Set artists from array
     */
    public void setArtistList(String[] artistList) {
        if (artistList == null || artistList.length == 0) {
            this.artists = null;
        } else {
            this.artists = String.join(", ", artistList);
        }
    }
    
    /**
     * Get number of tracks
     */
    public int getTrackCount() {
        return getTrackArray().length;
    }
    
    /**
     * Check if this is a compilation album (multiple artists)
     */
    public boolean isCompilation() {
        return getArtistList().length > 3; // More than 3 artists suggests compilation
    }
}