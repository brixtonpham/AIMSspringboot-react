package com.itss.ecommerce.repository;

import com.itss.ecommerce.entity.LP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LPRepository extends JpaRepository<LP, Long> {
}