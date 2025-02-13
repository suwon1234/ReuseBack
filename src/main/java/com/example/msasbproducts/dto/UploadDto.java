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
    private String email;
    private List<String> imageUrls;

    @Builder
    public UploadDto(List<String> imageUrls, String email) {
        this.imageUrls = imageUrls;
        this.email = email;
    }
}
