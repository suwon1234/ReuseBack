package com.example.msasbproducts.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 제품 테이블
 * 더미 데이터 추가
 insert into products (pdt_price, pdt_name, pdt_quantity) values ('1000000','macbook air','100')
 ,('2000000','macbook pro','100')
 ,('30000000','imac','100')
 ,('40000000','imac pro','100');
 */
@Entity
@Table(name="products")
@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor // 모든 필드를 사용하는 생성자
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore // JSON 응답 처리시 해당 필드 무시(누락)
    // 민감한 내부 데이터들 추가하여 처리!!
    private Integer pdtId;
    private Integer pdtPrice;
    private String pdtName;
    private Integer pdtQuantity;
    private String description;
    private String dtype;
    private String email;
//    @ElementCollection
//    private List<String> imageUrls;

    // 제품, 좋아요, 즐겨찾기, ....
//    @Builder
//    public ProductEntity(String pdtId, String pdtName, Integer pdtPrice, Integer pdtQuantity) {
//        this.pdtId = pdtId;
//        this.pdtName = pdtName;
//        this.pdtPrice = pdtPrice;
//        this.pdtQuantity = pdtQuantity;
//    }
}
