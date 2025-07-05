package com.itss.ecommerce.service.auth;

import com.itss.ecommerce.dto.mapper.UserMapper;
import com.itss.ecommerce.dto.user.UserDTO;
import com.itss.ecommerce.entity.User;
import com.itss.ecommerce.service.admin.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * Validates user login credentials
     * @param email User's email
     * @param password User's password (plain text)
     * @return UserDTO if credentials are valid, null otherwise
     */
    public UserDTO validateLogin(String email, String password) {
        try {
            Optional<User> userOpt = userService.authenticateUser(email, password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return userMapper.toDTO(user);
            }
            
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error validating login: " + e.getMessage(), e);
        }
    }

    /**
     * Gets current user details by user ID
     * @param userId User's ID
     * @return UserDTO if user exists and is active, null otherwise
     */
    public UserDTO getCurrentUser(Long userId) {
        try {
            Optional<User> userOpt = userService.getUserById(userId);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Check if user is still active
                if (user.getIsActive()) {
                    return userMapper.toDTO(user);
                }
            }
            
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error getting current user: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if a user is authenticated and has manager role
     * @param userId User's ID
     * @return true if user is authenticated manager, false otherwise
     */
    public boolean isManager(Long userId) {
        try {
            UserDTO user = getCurrentUser(userId);
            return user != null && "MANAGER".equals(user.getRole());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a user can manage products (admin or manager)
     * @param userId User's ID
     * @return true if user can manage products, false otherwise
     */
    public boolean canManageProducts(Long userId) {
        try {
            UserDTO user = getCurrentUser(userId);
            return user != null && ("ADMIN".equals(user.getRole()) || "MANAGER".equals(user.getRole()));
        } catch (Exception e) {
            return false;
        }
    }
}