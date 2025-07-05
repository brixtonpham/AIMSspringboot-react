package com.itss.ecommerce.controller;

import com.itss.ecommerce.dto.ApiResponse;
import com.itss.ecommerce.dto.UserDTO;
import com.itss.ecommerce.dto.UserProfileDTO;
import com.itss.ecommerce.dto.mapper.UserMapper;
import com.itss.ecommerce.service.auth.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserMapper userMapper;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserProfileDTO>> login(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            if (email == null || password == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Email and password are required"));
            }

            UserDTO userDTO = authService.validateLogin(email, password);
            UserProfileDTO userProfileDTO = userMapper.toProfileDTO(userDTO);
            if (userDTO != null) {
                // Store user information in session
                session.setAttribute("userId", userDTO.getUserId());
                session.setAttribute("userEmail", userDTO.getEmail());
                session.setAttribute("userRole", userDTO.getRole());
                
                return ResponseEntity.ok(ApiResponse.success(userProfileDTO, "Login successful"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.unauthorized("Invalid email or password"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.internalError("Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        try {
            session.invalidate();
            return ResponseEntity.ok(ApiResponse.success("Logout successful"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.internalError("Logout failed: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.unauthorized("Not authenticated"));
            }

            UserDTO userDTO = authService.getCurrentUser(userId);
            if (userDTO != null) {
                return ResponseEntity.ok(ApiResponse.success(userDTO, "User retrieved successfully"));
            } else {
                return ResponseEntity.status(401)
                    .body(ApiResponse.unauthorized("User not found or inactive"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.internalError("Failed to get current user: " + e.getMessage()));
        }
    }
}