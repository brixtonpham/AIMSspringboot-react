package com.itss.ecommerce.dto.mapper;

import com.itss.ecommerce.dto.*;
import com.itss.ecommerce.dto.user.CreateUserDTO;
import com.itss.ecommerce.dto.user.UserDTO;
import com.itss.ecommerce.dto.user.UserProfileDTO;
import com.itss.ecommerce.entity.User;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    
    /**
     * Convert User entity to UserDTO
     */
    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole() != null ? user.getRole().toString() : null);
        dto.setIsActive(user.getIsActive());
        dto.setSalary(user.getSalary());
        dto.setRegistrationDate(user.getRegistrationDate());
        dto.setCreatedAt(user.getRegistrationDate());
        dto.setUpdatedAt(user.getRegistrationDate());
        
        return dto;
    }
    
    /**
     * Convert UserDTO to User entity
     */
    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole() != null ? User.UserRole.valueOf(dto.getRole()) : null);
        user.setIsActive(dto.getIsActive());
        user.setSalary(dto.getSalary());
        user.setRegistrationDate(dto.getRegistrationDate());
        
        return user;
    }
    
    /**
     * Convert list of User entities to UserDTOs
     */
    public static List<UserDTO> toDTOList(List<User> users) {
        if (users == null) return null;
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of UserDTOs to User entities
     */
    public static List<User> toEntityList(List<UserDTO> dtos) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(UserMapper::toEntity)
                .collect(Collectors.toList());
    }
    /**
     * Convert User entity to UserProfileDTO
     */
    public static UserProfileDTO toProfileDTO(User user) {
        if (user == null) return null;
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUserId(user.getUserId());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setName(user.getName());
        profileDTO.setIsActive(user.getIsActive());
        profileDTO.setPhone(user.getPhone());
        profileDTO.setRole(user.getRole() != null ? user.getRole().toString() : null);
        return profileDTO;
    }
    /**
     * Convert UserDTO to UserProfileDTO
     */
    public static UserProfileDTO toProfileDTO(UserDTO userDTO) {
        if (userDTO == null) return null;
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUserId(userDTO.getUserId());
        profileDTO.setEmail(userDTO.getEmail());
        profileDTO.setName(userDTO.getName());
        profileDTO.setIsActive(userDTO.getIsActive());
        profileDTO.setPhone(userDTO.getPhone());
        profileDTO.setRole(userDTO.getRole());
        return profileDTO;  
    }
    
    /**
     * Convert CreateUserDTO to User entity
     */
    public static User toEntity(CreateUserDTO dto) {
        if (dto == null) return null;
        
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole() != null ? User.UserRole.valueOf(dto.getRole()) : null);
        user.setIsActive(dto.getIsActive());
        user.setSalary(dto.getSalary());
        
        return user;
    }
}