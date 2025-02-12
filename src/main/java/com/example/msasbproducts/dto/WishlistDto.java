package com.example.msasbproducts.dto;

import lombok.*;

@Data
@NoArgsConstructor
@ToString
public class WishlistDto {
    private Integer id;
    private String email;
    private Integer pdtId;
    private String pdtName;
    private Integer pdtPrice;



    @Builder
    public WishlistDto(Integer id, String email, Integer pdtId, String pdtName, Integer pdtPrice){
        this.id = id;
        this.email = email;
        this.pdtId = pdtId;
        this.pdtName = pdtName;
        this.pdtPrice = pdtPrice;
    }

}
