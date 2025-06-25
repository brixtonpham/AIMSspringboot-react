package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "lp")
@Data
@EqualsAndHashCode(callSuper = true)
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
}