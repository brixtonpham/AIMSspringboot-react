package com.itss.ecommerce.dto.mapper;

import com.itss.ecommerce.dto.*;
import com.itss.ecommerce.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    
    /**
     * Convert User entity to UserDTO
     */
    public UserDTO toDTO(User user) {
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
    public User toEntity(UserDTO dto) {
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
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) return null;
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of UserDTOs to User entities
     */
    public List<User> toEntityList(List<UserDTO> dtos) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}