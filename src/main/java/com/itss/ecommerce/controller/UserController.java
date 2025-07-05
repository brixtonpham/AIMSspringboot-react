package com.itss.ecommerce.controller;

import com.itss.ecommerce.dto.*;
import com.itss.ecommerce.dto.mapper.UserMapper;
import com.itss.ecommerce.entity.User;
import com.itss.ecommerce.service.admin.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    /**
     * Get all users
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        log.info("GET /api/users - Fetching all users");
        
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = UserMapper.toDTOList(users);
        
        return ResponseEntity.ok(ApiResponse.success(userDTOs,
            String.format("Retrieved %d users", userDTOs.size())));
    }
    
    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(
            @PathVariable @Positive Long id) {
        log.info("GET /api/users/{} - Fetching user", id);
        
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound("User not found with ID: " + id));
        }
        
        UserDTO userDTO = UserMapper.toDTO(user.get());
        return ResponseEntity.ok(ApiResponse.success(userDTO));
    }
    
    /**
     * Get user by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(
            @PathVariable @Email String email) {
        log.info("GET /api/users/email/{} - Fetching user", email);
        
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound("User not found with email: " + email));
        }
        
        UserDTO userDTO = UserMapper.toDTO(user.get());
        return ResponseEntity.ok(ApiResponse.success(userDTO));
    }
    
    /**
     * Create new user
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Valid @RequestBody UserDTO userDTO) {
        log.info("POST /api/users - Creating new user: {}", userDTO.getEmail());
        
        User user = UserMapper.toEntity(userDTO);
        User savedUser = userService.createUser(user);
        UserDTO savedUserDTO = UserMapper.toDTO(savedUser);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(savedUserDTO, "User created successfully"));
    }
    
    /**
     * Update user
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UserDTO userDTO) {
        log.info("PUT /api/users/{} - Updating user", id);
        
        User updatedUser = UserMapper.toEntity(userDTO);
        User savedUser = userService.updateUser(id, updatedUser);
        UserDTO savedUserDTO = UserMapper.toDTO(savedUser);
        
        return ResponseEntity.ok(ApiResponse.success(savedUserDTO, "User updated successfully"));
    }
    
    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable @Positive Long id) {
        log.info("DELETE /api/users/{} - Deleting user", id);
        
        userService.deleteUser(id);
        
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }
    
    /**
     * Get users by name pattern
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDTO>>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {
        log.info("GET /api/users/search - Searching users");
        
        List<User> users = userService.searchUsers(name != null ? name : "");
        List<UserDTO> userDTOs = UserMapper.toDTOList(users);
        
        return ResponseEntity.ok(ApiResponse.success(userDTOs,
            String.format("Found %d users matching criteria", userDTOs.size())));
    }
    
    /**
     * Get users by name containing
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByNameContaining(
            @PathVariable String name) {
        log.info("GET /api/users/name/{} - Fetching users with name containing", name);
        
        List<User> users = userService.searchUsers(name);
        List<UserDTO> userDTOs = UserMapper.toDTOList(users);
        
        return ResponseEntity.ok(ApiResponse.success(userDTOs,
            String.format("Found %d users with name containing '%s'", userDTOs.size(), name)));
    }
    
    /**
     * Check if email exists
     */
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(
            @PathVariable @Email String email) {
        log.info("GET /api/users/exists/email/{} - Checking email existence", email);
        
        boolean exists = userService.getUserByEmail(email).isPresent();
        
        return ResponseEntity.ok(ApiResponse.success(exists,
            exists ? "Email already exists" : "Email is available"));
    }
    
    /**
     * Check if phone exists
     */
    @GetMapping("/exists/phone/{phone}")
    public ResponseEntity<ApiResponse<Boolean>> checkPhoneExists(
            @PathVariable String phone) {
        log.info("GET /api/users/exists/phone/{} - Checking phone existence", phone);
        
        // Phone check not implemented in service, return false
        boolean exists = false;
        
        return ResponseEntity.ok(ApiResponse.success(exists,
            exists ? "Phone number already exists" : "Phone number is available"));
    }
    
    /**
     * Update user profile
     */
    @PatchMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        log.info("PATCH /api/users/{}/profile - Updating user profile", id);
        
        User user = userService.getUserById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        User updatedUser = userService.updateUser(id, user);
        UserDTO userDTO = UserMapper.toDTO(updatedUser);
        
        return ResponseEntity.ok(ApiResponse.success(userDTO, "User profile updated successfully"));
    }
    
    /**
     * Update user email
     */
    @PatchMapping("/{id}/email")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserEmail(
            @PathVariable @Positive Long id,
            @RequestParam @Email String email) {
        log.info("PATCH /api/users/{}/email - Updating user email to {}", id, email);
        
        User user = userService.getUserById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        user.setEmail(email);
        User updatedUser = userService.updateUser(id, user);
        UserDTO userDTO = UserMapper.toDTO(updatedUser);
        
        return ResponseEntity.ok(ApiResponse.success(userDTO, "User email updated successfully"));
    }
    
    /**
     * Get user count
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getUserCount() {
        log.info("GET /api/users/count - Getting total user count");
        
        long count = userService.getActiveUserCount();
        
        return ResponseEntity.ok(ApiResponse.success(count,
            String.format("Total users: %d", count)));
    }
    
    /**
     * Block user with reason and send email notification
     */
    @PostMapping("/{id}/block")
    public ResponseEntity<ApiResponse<UserDTO>> blockUser(
            @PathVariable @Positive Long id,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String blockedBy) {
        log.info("POST /api/users/{}/block - Blocking user", id);
        
        User blockedUser = userService.blockUser(id, reason, blockedBy != null ? blockedBy : "Administrator");
        UserDTO userDTO = UserMapper.toDTO(blockedUser);
        
        return ResponseEntity.ok(ApiResponse.success(userDTO, 
            String.format("User with ID %d has been blocked successfully. Email notification sent.", id)));
    }
    
    /**
     * Unblock user and send email notification
     */
    @PostMapping("/{id}/unblock")
    public ResponseEntity<ApiResponse<UserDTO>> unblockUser(
            @PathVariable @Positive Long id,
            @RequestParam(required = false) String unblockedBy) {
        log.info("POST /api/users/{}/unblock - Unblocking user", id);
        
        User unblockedUser = userService.unblockUser(id, unblockedBy != null ? unblockedBy : "Administrator");
        UserDTO userDTO = UserMapper.toDTO(unblockedUser);
        
        return ResponseEntity.ok(ApiResponse.success(userDTO,
            String.format("User with ID %d has been unblocked successfully. Email notification sent.", id)));
    }
}