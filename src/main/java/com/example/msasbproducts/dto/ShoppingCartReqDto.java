package com.example.msasbproducts.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 통신용 Dto, 장바구니에 제품을 담을때 정보 세팅, 필요시 장바구니 목록 보여주기도 활용 가능
 * - 현재는 제품 엔티티와 유사, 제품 DTO와 거의 동일 => 차후 확장 가능성 존재, 구분하기위해서 생성함aasa
 */
@Data
@NoArgsConstructor
@ToString
public class ShoppingCartReqDto {
    private Integer pdtId;
    private Integer pdtPrice;
    private String pdtName;
    private Integer pdtQuantity;
    // 모든 값을 세팅하는 생성자 -> 빌더패턴 지원
    @Builder
    public ShoppingCartReqDto(Integer pdtId, String pdtName, Integer pdtPrice, Integer pdtQuantity) {
        this.pdtId = pdtId;
        this.pdtName = pdtName;
        this.pdtPrice = pdtPrice;
        this.pdtQuantity = pdtQuantity;
    }
    // id 세팅하지 않는 생성자 -> 빌더패턴 지원
    @Builder
    public ShoppingCartReqDto(String pdtName, Integer pdtPrice, Integer pdtQuantity) {
        this.pdtName = pdtName;
        this.pdtPrice = pdtPrice;
        this.pdtQuantity = pdtQuantity;
    }
}
