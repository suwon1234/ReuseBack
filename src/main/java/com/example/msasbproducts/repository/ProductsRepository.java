package com.example.msasbproducts.repository;

import com.example.msasbproducts.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductsRepository extends JpaRepository<ProductEntity, Integer> {
}
