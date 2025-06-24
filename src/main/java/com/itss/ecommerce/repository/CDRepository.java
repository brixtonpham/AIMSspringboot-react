package com.itss.ecommerce.repository;

import com.itss.ecommerce.entity.CD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CDRepository extends JpaRepository<CD, Long> {
    
    /**
     * Find CDs by genre
     */
    List<CD> findByGenre(String genre);
    
    /**
     * Find CDs by artist (contains search)
     */
    @Query("SELECT c FROM CD c WHERE LOWER(c.artists) LIKE LOWER(CONCAT('%', :artist, '%'))")
    List<CD> findByArtistContaining(@Param("artist") String artist);
    
    /**
     * Find CDs by record label
     */
    List<CD> findByRecordLabel(String recordLabel);
    
    /**
     * Find CDs released in a specific year
     */
    @Query("SELECT c FROM CD c WHERE c.releaseDate LIKE :year%")
    List<CD> findByReleaseYear(@Param("year") String year);
    
    /**
     * Find CDs by multiple genres
     */
    @Query("SELECT c FROM CD c WHERE c.genre IN :genres")
    List<CD> findByGenres(@Param("genres") List<String> genres);
    
    /**
     * Find CDs with track list containing specific track
     */
    @Query("SELECT c FROM CD c WHERE LOWER(c.trackList) LIKE LOWER(CONCAT('%', :track, '%'))")
    List<CD> findByTrackContaining(@Param("track") String track);
    
    /**
     * Search CDs by title, artist, or genre
     */
    @Query("SELECT c FROM CD c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.artists) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.genre) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<CD> searchCDs(@Param("search") String searchTerm);
    
    /**
     * Find CDs by record label and genre
     */
    List<CD> findByRecordLabelAndGenre(String recordLabel, String genre);
    
    /**
     * Find compilation albums (multiple artists)
     */
    @Query("SELECT c FROM CD c WHERE c.artists LIKE '%,%,%'")
    List<CD> findCompilationAlbums();
    
    /**
     * Find CDs by release date range
     */
    @Query("SELECT c FROM CD c WHERE c.releaseDate BETWEEN :startDate AND :endDate")
    List<CD> findByReleaseDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
}