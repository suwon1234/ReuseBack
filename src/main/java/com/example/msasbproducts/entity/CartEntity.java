package com.example.msasbproducts.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 장바구니에 담기는 정보
 */
@Entity
@Table(name="cart")
@Data
@NoArgsConstructor
@ToString
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer cartId;
    private Integer pdtId;

    private String pdtName;
    private Integer price;    // 총액 = 제품단가 * 개수
    private Integer quantity; // 수량 = 장바구니에 동일 제품의 담긴 개수

    private String email;     // 장바구니에 담긴 제품의 소유주(임시)

    @Builder
    public CartEntity(Integer pdtId, String pdtName, Integer price, Integer quantity, String email) {
        this.pdtId = pdtId;
        this.pdtName = pdtName;
        this.price = price;
        this.quantity = quantity;
        this.email = email;
    }
}
