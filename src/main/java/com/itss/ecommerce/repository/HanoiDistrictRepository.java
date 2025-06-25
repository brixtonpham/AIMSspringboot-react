package com.itss.ecommerce.repository;

import com.itss.ecommerce.entity.HanoiDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HanoiDistrictRepository extends JpaRepository<HanoiDistrict, Long> {
    
    Optional<HanoiDistrict> findByDistrictNameIgnoreCase(String districtName);
    
    boolean existsByDistrictNameIgnoreCase(String districtName);
}