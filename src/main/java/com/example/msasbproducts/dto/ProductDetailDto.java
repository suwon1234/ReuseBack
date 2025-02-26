package com.example.msasbproducts.dto;

import lombok.*;

import java.util.List;

/**
 * 제품 상세 정보 요청시 데이터를 담는 그릇
 * dasdadasda
 */
@Data
@NoArgsConstructor
@ToString
public class ProductDetailDto {
    // id 부분을 노출할지 숨길지 논의가 필요(컨셉)
    private Integer pdtId;
    private Integer pdtPrice;
    private String pdtName;
    private Integer pdtQuantity;
    private String description;
    private String dtype;
    private String email;
    private List<String> imageUrls;
    @Builder
    public ProductDetailDto(Integer ptId, Integer ptPrice, String ptName, Integer ptQuantity, String description, String dtype, String email, List<String> imageUrls) {
        this.pdtId = ptId;
        this.pdtPrice = ptPrice;
        this.pdtName = ptName;
        this.pdtQuantity = ptQuantity;
        this.description = description;
        this.dtype = dtype;
        this.email = email;
        this.imageUrls = imageUrls;
    }
}
