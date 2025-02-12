package com.example.msasbproducts.repository;

import com.example.msasbproducts.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<ProductEntity, Integer> {
}
