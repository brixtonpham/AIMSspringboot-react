package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "books")
@DiscriminatorValue("book")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Book extends Product {
    
    
    @Column(name = "genre", length = 100)
    private String genre;
    
    @Column(name = "page_count")
    private Integer pageCount;
    
    @Column(name = "publication_date")
    private String publicationDate;
    
    @Column(name = "authors", length = 500)
    private String authors;
    
    @Column(name = "publishers", length = 200)
    private String publishers;
    
    @Column(name = "cover_type", length = 50)
    private String coverType;
    
    @Override
    public String getProductType() {
        return "book";
    }
    
    /**
     * Get author list as array (split by comma)
     */
    public String[] getAuthorList() {
        if (authors == null || authors.trim().isEmpty()) {
            return new String[0];
        }
        return authors.split(",\\s*");
    }
    
    /**
     * Set authors from array
     */
    public void setAuthorList(String[] authorList) {
        if (authorList == null || authorList.length == 0) {
            this.authors = null;
        } else {
            this.authors = String.join(", ", authorList);
        }
    }
    
    /**
     * Check if book is hardcover
     */
    public boolean isHardcover() {
        return "hardcover".equalsIgnoreCase(coverType);
    }
    
    /**
     * Get reading time estimation (assuming 250 words per page, 200 wpm)
     */
    public Integer getEstimatedReadingTimeMinutes() {
        if (pageCount == null || pageCount <= 0) {
            return null;
        }
        // Rough estimation: 250 words per page, reading speed 200 wpm
        return (pageCount * 250) / 200;
    }
}