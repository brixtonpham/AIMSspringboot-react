package com.itss.ecommerce.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO extends ProductDTO {
    
    private Long bookId;
    
    @Size(max = 100, message = "Genre must not exceed 100 characters")
    private String genre;
    
    @Positive(message = "Page count must be positive")
    private Integer pageCount;
    
    private String publicationDate;
    
    @Size(max = 500, message = "Authors must not exceed 500 characters")
    private String authors;
    
    @Size(max = 200, message = "Publishers must not exceed 200 characters")
    private String publishers;
    
    @Size(max = 50, message = "Cover type must not exceed 50 characters")
    private String coverType;
    
    // Additional computed fields
    private String[] authorList;
    private Boolean isHardcover;
    private Integer estimatedReadingTimeMinutes;
    
    /**
     * Get author list as array
     */
    public String[] getAuthorList() {
        if (authors == null || authors.trim().isEmpty()) {
            return new String[0];
        }
        return authors.split(",\\s*");
    }
    
    /**
     * Check if book is hardcover
     */
    public Boolean getIsHardcover() {
        return "hardcover".equalsIgnoreCase(coverType);
    }
    
    /**
     * Get estimated reading time in minutes
     */
    public Integer getEstimatedReadingTimeMinutes() {
        if (pageCount == null || pageCount <= 0) {
            return null;
        }
        // Rough estimation: 250 words per page, reading speed 200 wpm
        return (pageCount * 250) / 200;
    }
    
    /**
     * Get formatted reading time
     */
    public String getFormattedReadingTime() {
        Integer minutes = getEstimatedReadingTimeMinutes();
        if (minutes == null) return "Unknown";
        
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, remainingMinutes);
        } else {
            return String.format("%dm", remainingMinutes);
        }
    }
}