package com.itss.ecommerce.service.handler;

import com.itss.ecommerce.entity.Book;
import com.itss.ecommerce.entity.Product;
import com.itss.ecommerce.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookHandler implements ProductTypeHandler {
    
    private final BookRepository bookRepository;
    
    @Override
    public boolean canHandle(Product product) {
        return product instanceof Book;
    }
    
    @Override
    public Product save(Product product) {
        return bookRepository.save((Book) product);
    }
    
    @Override
    public void updateTypeSpecificFields(Product existing, Product updated) {
        if (existing instanceof Book && updated instanceof Book) {
            Book existingBook = (Book) existing;
            Book updatedBook = (Book) updated;
            
            existingBook.setGenre(updatedBook.getGenre());
            existingBook.setPageCount(updatedBook.getPageCount());
            existingBook.setPublicationDate(updatedBook.getPublicationDate());
            existingBook.setAuthors(updatedBook.getAuthors());
            existingBook.setPublishers(updatedBook.getPublishers());
            existingBook.setCoverType(updatedBook.getCoverType());
        }
    }
}