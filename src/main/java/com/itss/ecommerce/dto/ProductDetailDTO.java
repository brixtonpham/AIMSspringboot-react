package com.itss.ecommerce.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductDetailDTO {
    private Long productId;
    private String title;
    private String category;
    private Integer price;
    private Integer productValue;
    private Integer quantity;
    private Float weight;
    private String imageUrl;
    private String introduction;
    private String barcode;
    private String importDate;
    private String dimensions;
    private String condition;
    private Boolean rushOrderSupported;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String stockStatus;
    
    // Media-specific attributes
    // Book attributes
    private String author;
    private String publisher;
    private String coverType;
    private Integer pages;
    private String language;
    private String publicationDate;
    
    // CD/LP attributes
    private String artist;
    private String recordLabel;
    private String musicType;
    private String releaseDate;
    private String tracklist;
    
    // DVD attributes
    private String director;
    private Integer runtime;
    private String discType;
    private String studio;
    private String subtitles;
    private String languageDvd;
    
    // LP specific attributes
    private Integer rpm;
    private Double sizeInches;
    private String vinylCondition;
    private String sleeveCondition;
    
    // Helper method to determine stock status
    public String getStockStatus() {
        if (quantity == null || quantity <= 0) {
            return "Out of Stock";
        } else if (quantity <= 5) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }
}