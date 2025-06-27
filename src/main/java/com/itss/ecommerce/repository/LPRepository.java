package com.itss.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itss.ecommerce.entity.LP;

@Repository
public interface LPRepository extends JpaRepository<LP, Long> {
}