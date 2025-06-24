package com.itss.ecommerce.repository;

import com.itss.ecommerce.entity.DVD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DVDRepository extends JpaRepository<DVD, Long> {
    
    /**
     * Find DVDs by genre
     */
    List<DVD> findByGenre(String genre);
    
    /**
     * Find DVDs by director (contains search)
     */
    @Query("SELECT d FROM DVD d WHERE LOWER(d.directors) LIKE LOWER(CONCAT('%', :director, '%'))")
    List<DVD> findByDirectorContaining(@Param("director") String director);
    
    /**
     * Find DVDs by studio
     */
    List<DVD> findByStudio(String studio);
    
    /**
     * Find DVDs by type (DVD, Blu-ray, etc.)
     */
    List<DVD> findByDvdType(String dvdType);
    
    /**
     * Find Blu-ray discs
     */
    @Query("SELECT d FROM DVD d WHERE LOWER(d.dvdType) LIKE '%blu%'")
    List<DVD> findBluRayDiscs();
    
    /**
     * Find 4K Ultra HD discs
     */
    @Query("SELECT d FROM DVD d WHERE LOWER(d.dvdType) LIKE '%4k%'")
    List<DVD> find4KDiscs();
    
    /**
     * Find DVDs by rating
     */
    List<DVD> findByRating(String rating);
    
    /**
     * Find child-friendly DVDs (G or PG)
     */
    @Query("SELECT d FROM DVD d WHERE d.rating IN ('G', 'PG')")
    List<DVD> findChildFriendlyDVDs();
    
    /**
     * Find DVDs by duration range
     */
    @Query("SELECT d FROM DVD d WHERE d.durationMinutes BETWEEN :minDuration AND :maxDuration")
    List<DVD> findByDurationRange(@Param("minDuration") int minDuration, @Param("maxDuration") int maxDuration);
    
    /**
     * Find feature-length films (over 75 minutes)
     */
    @Query("SELECT d FROM DVD d WHERE d.durationMinutes >= 75")
    List<DVD> findFeatureLengthFilms();
    
    /**
     * Find DVDs released in a specific year
     */
    @Query("SELECT d FROM DVD d WHERE d.releaseDate LIKE :year%")
    List<DVD> findByReleaseYear(@Param("year") String year);
    
    /**
     * Find DVDs by multiple genres
     */
    @Query("SELECT d FROM DVD d WHERE d.genre IN :genres")
    List<DVD> findByGenres(@Param("genres") List<String> genres);
    
    /**
     * Search DVDs by title, director, or genre
     */
    @Query("SELECT d FROM DVD d WHERE " +
           "LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.directors) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.genre) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<DVD> searchDVDs(@Param("search") String searchTerm);
    
    /**
     * Find DVDs by studio and genre
     */
    List<DVD> findByStudioAndGenre(String studio, String genre);
    
    /**
     * Find recent releases (within last year)
     */
    @Query("SELECT d FROM DVD d WHERE d.releaseDate >= :cutoffYear")
    List<DVD> findRecentReleases(@Param("cutoffYear") String cutoffYear);
}