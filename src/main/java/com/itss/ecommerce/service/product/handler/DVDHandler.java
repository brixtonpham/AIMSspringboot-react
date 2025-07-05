package com.itss.ecommerce.service.product.handler;

import com.itss.ecommerce.entity.DVD;
import com.itss.ecommerce.entity.Product;
import com.itss.ecommerce.repository.DVDRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DVDHandler implements ProductTypeHandler {
    
    private final DVDRepository dvdRepository;
    
    @Override
    public boolean canHandle(Product product) {
        return product instanceof DVD;
    }
    
    @Override
    public Product save(Product product) {
        return dvdRepository.save((DVD) product);
    }
    
    @Override
    public void updateTypeSpecificFields(Product existing, Product updated) {
        if (existing instanceof DVD && updated instanceof DVD) {
            DVD existingDVD = (DVD) existing;
            DVD updatedDVD = (DVD) updated;
            
            existingDVD.setReleaseDate(updatedDVD.getReleaseDate());
            existingDVD.setDvdType(updatedDVD.getDvdType());
            existingDVD.setGenre(updatedDVD.getGenre());
            existingDVD.setStudio(updatedDVD.getStudio());
            existingDVD.setDirectors(updatedDVD.getDirectors());
            existingDVD.setDurationMinutes(updatedDVD.getDurationMinutes());
            existingDVD.setRating(updatedDVD.getRating());
        }
    }
}