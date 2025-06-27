package com.itss.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itss.ecommerce.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email (for authentication)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find users by role
     */
    List<User> findByRole(User.UserRole role);
    
    /**
     * Find active users
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Find inactive users
     */
    List<User> findByIsActiveFalse();
    
    /**
     * Find users by name containing (case insensitive)
     */
    List<User> findByNameContainingIgnoreCase(String name);
    
    /**
     * Check if email already exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find admin users
     */
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND u.isActive = true")
    List<User> findActiveAdmins();
    
    /**
     * Find users who can manage products (admin or manager)
     */
    @Query("SELECT u FROM User u WHERE u.role IN ('ADMIN', 'MANAGER') AND u.isActive = true")
    List<User> findProductManagers();
    
    /**
     * Find users by phone number
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * Find customers (role = CUSTOMER)
     */
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER'")
    List<User> findCustomers();
    
    /**
     * Find employees with salary greater than specified amount
     */
    @Query("SELECT u FROM User u WHERE u.salary > :minSalary AND u.role IN ('ADMIN', 'MANAGER', 'EMPLOYEE')")
    List<User> findEmployeesWithSalaryGreaterThan(@Param("minSalary") Double minSalary);
    
    /**
     * Count users by role
     */
    long countByRole(User.UserRole role);
    
    /**
     * Count active users
     */
    long countByIsActiveTrue();
    
    /**
     * Search users by name or email
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<User> searchUsers(@Param("search") String searchTerm);
}