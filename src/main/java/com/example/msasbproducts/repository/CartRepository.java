package com.example.msasbproducts.repository;


import com.example.msasbproducts.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CartRepository extends JpaRepository<CartEntity, Integer> {
    // 고객별(email) 장바구니 조회
    // findBy + 컬럼명 => 특정 컬럼으로 조회하는 메소드 제공
    List<CartEntity> findByEmail(String email);
    // 장바구니에 수량 계산 => 이메일과 제품간 결합된 쿼리문 where pdtId=? and email=?
    Optional<CartEntity> findByEmailAndPdtIdIn(String email, List<Integer> pdtId);
}
