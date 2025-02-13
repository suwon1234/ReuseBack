package com.example.msasbproducts.dto;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class UploadDto {

    private List<String> imageUrls;

    @Builder
    public UploadDto(List<String> imageUrls){
        this.imageUrls = imageUrls;
    }
}
