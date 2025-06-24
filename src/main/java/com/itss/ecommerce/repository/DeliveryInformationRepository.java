package com.itss.ecommerce.repository;

import com.itss.ecommerce.entity.DeliveryInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryInformationRepository extends JpaRepository<DeliveryInformation, Long> {
    
    /**
     * Find delivery information by email
     */
    List<DeliveryInformation> findByEmail(String email);
    
    /**
     * Find delivery information by phone
     */
    List<DeliveryInformation> findByPhone(String phone);
    
    /**
     * Find delivery information by province
     */
    List<DeliveryInformation> findByProvince(String province);
    
    /**
     * Find delivery information by name (case insensitive)
     */
    List<DeliveryInformation> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find delivery information by address containing (case insensitive)
     */
    @Query("SELECT d FROM DeliveryInformation d WHERE LOWER(d.address) LIKE LOWER(CONCAT('%', :address, '%'))")
    List<DeliveryInformation> findByAddressContaining(@Param("address") String address);
    
    /**
     * Find delivery info with delivery fee greater than specified amount
     */
    List<DeliveryInformation> findByDeliveryFeeGreaterThan(int deliveryFee);
    
    /**
     * Find delivery info with delivery fee equal to zero (free delivery)
     */
    @Query("SELECT d FROM DeliveryInformation d WHERE d.deliveryFee = 0")
    List<DeliveryInformation> findFreeDeliveries();
    
    /**
     * Find unique provinces with delivery
     */
    @Query("SELECT DISTINCT d.province FROM DeliveryInformation d ORDER BY d.province")
    List<String> findUniqueProvinces();
    
    /**
     * Count deliveries by province
     */
    long countByProvince(String province);
    
    /**
     * Find delivery information by customer contact (email or phone)
     */
    @Query("SELECT d FROM DeliveryInformation d WHERE d.email = :contact OR d.phone = :contact")
    List<DeliveryInformation> findByEmailOrPhone(@Param("contact") String contact);
    
    /**
     * Find delivery information with special instructions
     */
    @Query("SELECT d FROM DeliveryInformation d WHERE d.deliveryMessage IS NOT NULL AND d.deliveryMessage != ''")
    List<DeliveryInformation> findWithDeliveryInstructions();
    
    /**
     * Search delivery information by name, email, or address
     */
    @Query("SELECT d FROM DeliveryInformation d WHERE " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.address) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<DeliveryInformation> searchDeliveryInfo(@Param("search") String searchTerm);
    
    /**
     * Get delivery fee statistics by province
     */
    @Query("SELECT d.province, COUNT(d), AVG(d.deliveryFee), MIN(d.deliveryFee), MAX(d.deliveryFee) " +
           "FROM DeliveryInformation d GROUP BY d.province ORDER BY d.province")
    List<Object[]> getDeliveryFeeStatisticsByProvince();
}