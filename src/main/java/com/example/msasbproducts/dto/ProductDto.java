package org.example.msasbproducts.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * 1차적 제품 노출시키는 제품 정보를 가진 dto
 *  - 이름, 가격 (전체 필드중에서 핵심만 노출)
 *  - 나머지 정보는 상세보기에서 노출
 */
@Data
@ToString
public class ProductDto {
    private String pdtName;
    private Integer pdtPrice;
    @Builder
    public ProductDto(String pdtName, Integer pdtPrice) {
        this.pdtName = pdtName;
        this.pdtPrice = pdtPrice;
    }
}
