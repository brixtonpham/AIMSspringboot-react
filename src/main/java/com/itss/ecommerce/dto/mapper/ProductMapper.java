package com.itss.ecommerce.dto.mapper;

import com.itss.ecommerce.dto.*;
import com.itss.ecommerce.dto.product.BookDTO;
import com.itss.ecommerce.dto.product.CDDTO;
import com.itss.ecommerce.dto.product.DVDDTO;
import com.itss.ecommerce.dto.product.LPDTO;
import com.itss.ecommerce.entity.*;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    
    /**
     * Convert Product entity to ProductDTO
     */
    public static ProductDTO toDTO(Product product) {
        if (product == null) return null;
        
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setTitle(product.getTitle());
        dto.setPrice(product.getPrice());
        dto.setWeight(product.getWeight());
        dto.setRushOrderSupported(product.getRushOrderSupported());
        dto.setImageUrl(product.getImageUrl());
        dto.setBarcode(product.getBarcode());
        dto.setImportDate(product.getImportDate());
        dto.setIntroduction(product.getIntroduction());
        dto.setQuantity(product.getQuantity());
        dto.setType(product.getProductType());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * Convert Book entity to BookDTO
     */
    public static BookDTO toDTO(Book book) {
        if (book == null) return null;
        
        BookDTO dto = new BookDTO();
        
        // Copy product fields
        copyProductFields(book, dto);
        
        // Copy book-specific fields
        dto.setBookId(book.getProductId());
        dto.setGenre(book.getGenre());
        dto.setPageCount(book.getPageCount());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setAuthors(book.getAuthors());
        dto.setPublishers(book.getPublishers());
        dto.setCoverType(book.getCoverType());
        
        return dto;
    }
    
    /**
     * Convert CD entity to CDDTO
     */
    public static CDDTO toDTO(CD cd) {
        if (cd == null) return null;
        
        CDDTO dto = new CDDTO();
        
        // Copy product fields
        copyProductFields(cd, dto);
        
        // Copy CD-specific fields
        dto.setCdId(cd.getProductId());
        dto.setTrackList(cd.getTrackList());
        dto.setGenre(cd.getGenre());
        dto.setRecordLabel(cd.getRecordLabel());
        dto.setArtists(cd.getArtists());
        dto.setReleaseDate(cd.getReleaseDate());
        
        return dto;
    }
    
    /**
     * Convert DVD entity to DVDDTO
     */
    public static DVDDTO toDTO(DVD dvd) {
        if (dvd == null) return null;
        
        DVDDTO dto = new DVDDTO();
        
        // Copy product fields
        copyProductFields(dvd, dto);
        
        // Copy DVD-specific fields
        dto.setDvdId(dvd.getProductId());
        dto.setReleaseDate(dvd.getReleaseDate());
        dto.setDvdType(dvd.getDvdType());
        dto.setGenre(dvd.getGenre());
        dto.setStudio(dvd.getStudio());
        dto.setDirectors(dvd.getDirectors());
        dto.setDurationMinutes(dvd.getDurationMinutes());
        dto.setRating(dvd.getRating());
        
        return dto;
    }
    
    /**
     * Convert LP entity to LPDTO
     */
    public static LPDTO toDTO(LP lp) {
        if (lp == null) return null;
        
        LPDTO dto = new LPDTO();
        
        // Copy product fields
        copyProductFields(lp, dto);
        
        // Copy LP-specific fields
        dto.setLpId(lp.getProductId());
        dto.setArtist(lp.getArtist());
        dto.setRecordLabel(lp.getRecordLabel());
        dto.setMusicType(lp.getMusicType());
        dto.setReleaseDate(lp.getReleaseDate());
        dto.setTracklist(lp.getTracklist());
        dto.setRpm(lp.getRpm());
        dto.setSizeInches(lp.getSizeInches());
        dto.setVinylCondition(lp.getVinylCondition());
        dto.setSleeveCondition(lp.getSleeveCondition());
        
        return dto;
    }
    
    /**
     * Convert ProductDTO to Product entity (for polymorphic handling)
     */
    public static Product toEntity(ProductDTO dto, String productType) {
        if (dto == null) return null;
        
        Product product = switch (productType.toLowerCase()) {
            case "book" -> new Book();
            case "cd" -> new CD();
            case "dvd" -> new DVD();
            case "lp" -> new LP();
            default -> throw new IllegalArgumentException("Unknown product type: " + productType);
        };
        
        copyDTOToEntity(dto, product);
        return product;
    }
    
    /**
     * Convert BookDTO to Book entity
     */
    public static Book toEntity(BookDTO dto) {
        if (dto == null) return null;
        
        Book book = new Book();
        copyDTOToEntity(dto, book);
        
        book.setProductId(dto.getBookId());
        book.setGenre(dto.getGenre());
        book.setPageCount(dto.getPageCount());
        book.setPublicationDate(dto.getPublicationDate());
        book.setAuthors(dto.getAuthors());
        book.setPublishers(dto.getPublishers());
        book.setCoverType(dto.getCoverType());
        
        return book;
    }
    
    /**
     * Convert CDDTO to CD entity
     */
    public static CD toEntity(CDDTO dto) {
        if (dto == null) return null;
        
        CD cd = new CD();
        copyDTOToEntity(dto, cd);
        
        cd.setProductId(dto.getCdId());
        cd.setTrackList(dto.getTrackList());
        cd.setGenre(dto.getGenre());
        cd.setRecordLabel(dto.getRecordLabel());
        cd.setArtists(dto.getArtists());
        cd.setReleaseDate(dto.getReleaseDate());
        
        return cd;
    }
    
    /**
     * Convert DVDDTO to DVD entity
     */
    public static DVD toEntity(DVDDTO dto) {
        if (dto == null) return null;
        
        DVD dvd = new DVD();
        copyDTOToEntity(dto, dvd);
        
        dvd.setProductId(dto.getDvdId());
        dvd.setReleaseDate(dto.getReleaseDate());
        dvd.setDvdType(dto.getDvdType());
        dvd.setGenre(dto.getGenre());
        dvd.setStudio(dto.getStudio());
        dvd.setDirectors(dto.getDirectors());
        dvd.setDurationMinutes(dto.getDurationMinutes());
        dvd.setRating(dto.getRating());
        
        return dvd;
    }
    
    /**
     * Convert LPDTO to LP entity
     */
    public static LP toEntity(LPDTO dto) {
        if (dto == null) return null;
        
        LP lp = new LP();
        copyDTOToEntity(dto, lp);
        
        lp.setProductId(dto.getLpId());
        lp.setArtist(dto.getArtist());
        lp.setRecordLabel(dto.getRecordLabel());
        lp.setMusicType(dto.getMusicType());
        lp.setReleaseDate(dto.getReleaseDate());
        lp.setTracklist(dto.getTracklist());
        lp.setRpm(dto.getRpm());
        lp.setSizeInches(dto.getSizeInches());
        lp.setVinylCondition(dto.getVinylCondition());
        lp.setSleeveCondition(dto.getSleeveCondition());
        
        return lp;
    }
    
    /**
     * Convert list of Product entities to ProductDTOs
     */
    public static List<ProductDTO> toDTOList(List<Product> products) {
        if (products == null) return null;
        return products.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of Book entities to BookDTOs
     */
    public static List<BookDTO> toBookDTOList(List<Book> books) {
        if (books == null) return null;
        return books.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of CD entities to CDDTOs
     */
    public static List<CDDTO> toCDDTOList(List<CD> cds) {
        if (cds == null) return null;
        return cds.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of DVD entities to DVDDTOs
     */
    public static List<DVDDTO> toDVDDTOList(List<DVD> dvds) {
        if (dvds == null) return null;
        return dvds.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of LP entities to LPDTOs
     */
    public static List<LPDTO> toLPDTOList(List<LP> lps) {
        if (lps == null) return null;
        return lps.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Copy common product fields from entity to DTO
     */
    private static void copyProductFields(Product entity, ProductDTO dto) {
        dto.setProductId(entity.getProductId());
        dto.setTitle(entity.getTitle());
        dto.setPrice(entity.getPrice());
        dto.setWeight(entity.getWeight());
        dto.setRushOrderSupported(entity.getRushOrderSupported());
        dto.setImageUrl(entity.getImageUrl());
        dto.setBarcode(entity.getBarcode());
        dto.setImportDate(entity.getImportDate());
        dto.setIntroduction(entity.getIntroduction());
        dto.setQuantity(entity.getQuantity());
        dto.setType(entity.getProductType());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
    }
    
    /**
     * Copy common product fields from DTO to entity
     */
    private static void copyDTOToEntity(ProductDTO dto, Product entity) {
        entity.setProductId(dto.getProductId());
        entity.setTitle(dto.getTitle());
        entity.setPrice(dto.getPrice());
        entity.setWeight(dto.getWeight());
        entity.setRushOrderSupported(dto.getRushOrderSupported());
        entity.setImageUrl(dto.getImageUrl());
        entity.setBarcode(dto.getBarcode());
        entity.setImportDate(dto.getImportDate());
        entity.setIntroduction(dto.getIntroduction());
        entity.setQuantity(dto.getQuantity());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
    }
    
    /**
     * Create Product entity from ProductFormRequest
     */
    public static Product createProductFromRequest(ProductFormRequest request) {
        if (request == null || request.getProductData() == null) {
            throw new IllegalArgumentException("Product data is required");
        }
        
        ProductDTO productData = request.getProductData();
        
        if (request.isBook()) {
            BookDTO bookDTO = request.getBookData() != null ? request.getBookData() : new BookDTO();
            copyBaseDataToSpecificDTO(productData, bookDTO);
            return toEntity(bookDTO);
            
        } else if (request.isCD()) {
            CDDTO cdDTO = request.getCdData() != null ? request.getCdData() : new CDDTO();
            copyBaseDataToSpecificDTO(productData, cdDTO);
            return toEntity(cdDTO);
            
        } else if (request.isDVD()) {
            DVDDTO dvdDTO = request.getDvdData() != null ? request.getDvdData() : new DVDDTO();
            copyBaseDataToSpecificDTO(productData, dvdDTO);
            return toEntity(dvdDTO);
            
        } else if (request.isLP()) {
            LPDTO lpDTO = request.getLpData() != null ? request.getLpData() : new LPDTO();
            copyBaseDataToSpecificDTO(productData, lpDTO);
            return toEntity(lpDTO);
            
        } else {
            // Fallback to base product if unknown type
            return toEntity(productData, productData.getType());
        }
    }
    
    /**
     * Copy base product data to specific DTO
     */
    public static void copyBaseDataToSpecificDTO(ProductDTO source, ProductDTO target) {
        target.setProductId(source.getProductId());
        target.setTitle(source.getTitle());
        target.setPrice(source.getPrice());
        target.setWeight(source.getWeight());
        target.setRushOrderSupported(source.getRushOrderSupported());
        target.setImageUrl(source.getImageUrl());
        target.setBarcode(source.getBarcode());
        target.setImportDate(source.getImportDate());
        target.setIntroduction(source.getIntroduction());
        target.setQuantity(source.getQuantity());
        target.setType(source.getType());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());
    }
    
    /**
     * Map Product to appropriate specific DTO
     */
    public static ProductDTO mapToSpecificDTO(Product product) {
        if (product instanceof Book) {
            return toDTO((Book) product);
        } else if (product instanceof CD) {
            return toDTO((CD) product);
        } else if (product instanceof DVD) {
            return toDTO((DVD) product);
        } else if (product instanceof LP) {
            return toDTO((LP) product);
        } else {
            return toDTO(product);
        }
    }
}