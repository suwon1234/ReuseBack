package com.example.msasbproducts.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="wishlist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class WishlistEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer Id;
    private String email;
    private Integer pdtId;
    private String pdtName;
    private Integer pdtPrice;
}
