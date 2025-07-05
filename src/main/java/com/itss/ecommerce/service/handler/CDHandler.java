package com.itss.ecommerce.service.handler;

import com.itss.ecommerce.entity.CD;
import com.itss.ecommerce.entity.Product;
import com.itss.ecommerce.repository.CDRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CDHandler implements ProductTypeHandler {
    
    private final CDRepository cdRepository;
    
    @Override
    public boolean canHandle(Product product) {
        return product instanceof CD;
    }
    
    @Override
    public Product save(Product product) {
        return cdRepository.save((CD) product);
    }
    
    @Override
    public void updateTypeSpecificFields(Product existing, Product updated) {
        if (existing instanceof CD && updated instanceof CD) {
            CD existingCD = (CD) existing;
            CD updatedCD = (CD) updated;
            
            existingCD.setTrackList(updatedCD.getTrackList());
            existingCD.setGenre(updatedCD.getGenre());
            existingCD.setRecordLabel(updatedCD.getRecordLabel());
            existingCD.setArtists(updatedCD.getArtists());
            existingCD.setReleaseDate(updatedCD.getReleaseDate());
        }
    }
}