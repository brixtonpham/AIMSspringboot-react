package com.itss.ecommerce.service.product.handler;

import com.itss.ecommerce.entity.LP;
import com.itss.ecommerce.entity.Product;
import com.itss.ecommerce.repository.LPRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LPHandler implements ProductTypeHandler {
    
    private final LPRepository lpRepository;
    
    @Override
    public boolean canHandle(Product product) {
        return product instanceof LP;
    }
    
    @Override
    public Product save(Product product) {
        return lpRepository.save((LP) product);
    }
    
    @Override
    public void updateTypeSpecificFields(Product existing, Product updated) {
        if (existing instanceof LP && updated instanceof LP) {
            LP existingLP = (LP) existing;
            LP updatedLP = (LP) updated;
            
            existingLP.setArtist(updatedLP.getArtist());
            existingLP.setRecordLabel(updatedLP.getRecordLabel());
            existingLP.setMusicType(updatedLP.getMusicType());
            existingLP.setReleaseDate(updatedLP.getReleaseDate());
            existingLP.setTracklist(updatedLP.getTracklist());
            existingLP.setRpm(updatedLP.getRpm());
            existingLP.setSizeInches(updatedLP.getSizeInches());
            existingLP.setVinylCondition(updatedLP.getVinylCondition());
            existingLP.setSleeveCondition(updatedLP.getSleeveCondition());
        }
    }
}