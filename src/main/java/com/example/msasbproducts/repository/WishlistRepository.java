package com.example.msasbproducts.repository;

import com.example.msasbproducts.entity.WishlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistEntity, Integer> {
    List<WishlistEntity> findByEmail(String email);

    Optional<WishlistEntity> findByEmailAndPdtId(String email, Integer pdtId);
}
