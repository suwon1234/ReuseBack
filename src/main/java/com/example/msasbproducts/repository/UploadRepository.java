package com.example.msasbproducts.repository;

import com.example.msasbproducts.entity.CartEntity;
import com.example.msasbproducts.entity.UploadEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UploadRepository extends JpaRepository<UploadEntity, Integer> {

    Optional<UploadEntity> findByEmail(String email);
}

