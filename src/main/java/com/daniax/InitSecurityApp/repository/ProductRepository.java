package com.daniax.InitSecurityApp.repository;

import com.daniax.InitSecurityApp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
