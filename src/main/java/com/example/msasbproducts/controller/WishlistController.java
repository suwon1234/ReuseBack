package com.example.msasbproducts.controller;

import com.example.msasbproducts.dto.WishlistDto;
import com.example.msasbproducts.entity.WishlistEntity;
import com.example.msasbproducts.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    // 특정 이메일의 위시리스트 조회
    @GetMapping("/{email}")
    public ResponseEntity<List<WishlistDto>> getWishlist(@PathVariable String email) {
        List<WishlistDto> wishlist = wishlistService.getWishlist(email);  // WishlistService가 List<WishlistEntity>를 반환한다고 가정
        return ResponseEntity.ok(wishlist);
    }

    // 위시리스트에 항목 추가
    @PostMapping
    public ResponseEntity<WishlistDto> addWishlist(@RequestBody WishlistEntity wishlistEntity) {
        WishlistDto addedWishlist = wishlistService.addWishlist(wishlistEntity);
        return ResponseEntity.status(201).body(addedWishlist);  // 201 Created 상태 코드
    }

    // 위시리스트에서 항목 제거
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeWishlist(@PathVariable Integer id) {
        wishlistService.removeWishlist(id);
        return ResponseEntity.noContent().build();  // 204 No Content 상태 코드
    }
}
