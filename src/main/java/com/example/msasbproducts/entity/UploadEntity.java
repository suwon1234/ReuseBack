package com.example.msasbproducts.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name ="upload-image")
@Data
@NoArgsConstructor
public class UploadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer pdtId;
    private String email;
    @ElementCollection
    private List<String> imageUrls;

    @Builder
    public UploadEntity(String email, List<String> imageUrls, Integer pdtId) {
        this.email = email;
        this.pdtId = pdtId;
        this.imageUrls = imageUrls;

    }
}
