package com.itss.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itss.ecommerce.entity.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    /**
     * Find books by genre
     */
    List<Book> findByGenre(String genre);
    
    /**
     * Find books by author (contains search)
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.authors) LIKE LOWER(CONCAT('%', :author, '%'))")
    List<Book> findByAuthorContaining(@Param("author") String author);
    
    /**
     * Find books by publisher
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.publishers) LIKE LOWER(CONCAT('%', :publisher, '%'))")
    List<Book> findByPublisher(@Param("publisher") String publisher);
    
    /**
     * Find books by page count range
     */
    @Query("SELECT b FROM Book b WHERE b.pageCount BETWEEN :minPages AND :maxPages")
    List<Book> findByPageCountRange(@Param("minPages") int minPages, @Param("maxPages") int maxPages);
    
    /**
     * Find books by cover type
     */
    List<Book> findByCoverType(String coverType);
    
    /**
     * Find hardcover books
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.coverType) = 'hardcover'")
    List<Book> findHardcoverBooks();
    
    /**
     * Find books published in a specific year
     */
    @Query("SELECT b FROM Book b WHERE b.publicationDate LIKE :year%")
    List<Book> findByPublicationYear(@Param("year") String year);
    
    /**
     * Find books with page count greater than specified
     */
    List<Book> findByPageCountGreaterThan(int pageCount);
    
    /**
     * Find books by multiple genres
     */
    @Query("SELECT b FROM Book b WHERE b.genre IN :genres")
    List<Book> findByGenres(@Param("genres") List<String> genres);
    
    /**
     * Search books by title, author, or genre
     */
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.authors) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.genre) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Book> searchBooks(@Param("search") String searchTerm);
}