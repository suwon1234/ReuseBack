package com.example.msasbproducts.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ProductReqDto {
    private String email;
    private Integer pdtId;

    @Builder
    public ProductReqDto(String email, Integer ptId) {
        this.email = email;
        this.pdtId = ptId;
    }
}
