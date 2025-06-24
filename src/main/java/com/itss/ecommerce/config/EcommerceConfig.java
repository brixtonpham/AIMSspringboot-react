package com.itss.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.itss.ecommerce.repository")
@EnableTransactionManagement
public class EcommerceConfig {
    
    // Configuration for the new refactored ecommerce package
    
}