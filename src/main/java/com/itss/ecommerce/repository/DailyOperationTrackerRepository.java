package com.itss.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itss.ecommerce.entity.DailyOperationTracker;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyOperationTrackerRepository extends JpaRepository<DailyOperationTracker, Long> {
    
    Optional<DailyOperationTracker> findByUserIdAndOperationDate(Long userId, LocalDate operationDate);
    
    Optional<DailyOperationTracker> findByUserIdAndIsEditingTrue(Long userId);
}