package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hanoi_district")
@Data
public class HanoiDistrict {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "district_name", nullable = false, unique = true)
    private String districtName;
    
    @Column(name = "supports_rush_delivery", nullable = false)
    private Boolean supportsRushDelivery = true;
}