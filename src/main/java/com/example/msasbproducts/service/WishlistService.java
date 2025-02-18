package com.example.msasbproducts.service;

import com.example.msasbproducts.dto.WishlistDto;
import com.example.msasbproducts.entity.WishlistEntity;
import com.example.msasbproducts.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    /**
     * 특정 사용자의 찜 목록 조회
     */
    public List<WishlistDto> getWishlist(String email) {
        List<WishlistEntity> wishlistEntities = wishlistRepository.findByEmail(email);
        return wishlistEntities.stream()
                .map(wishlist -> WishlistDto.builder()
                        .pdtName(wishlist.getPdtName())
                        .pdtId(wishlist.getPdtId())
                        .email(wishlist.getEmail())
                        .id(wishlist.getId())
                        .pdtPrice(wishlist.getPdtPrice())
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 찜 추가 (중복 방지)
     */
    public WishlistDto addWishlist(WishlistEntity wishlistDto) {
        // 이미 찜한 상품인지 확인
        Optional<WishlistEntity> existingWishlist = wishlistRepository.findByEmailAndPdtId(wishlistDto.getEmail(), wishlistDto.getPdtId());
        if (existingWishlist.isPresent()) {
            throw new IllegalArgumentException("이미 찜한 상품입니다.");
        }

        // 새로운 찜 상품 저장
        WishlistEntity wishlistEntity = new WishlistEntity();
        wishlistEntity.setEmail(wishlistDto.getEmail());
        wishlistEntity.setPdtId(wishlistDto.getPdtId());
        wishlistEntity.setPdtName(wishlistDto.getPdtName());
        wishlistEntity.setPdtPrice(wishlistDto.getPdtPrice());

        WishlistEntity savedEntity = wishlistRepository.save(wishlistEntity);
        return new WishlistDto(
                savedEntity.getId(),
                savedEntity.getEmail(),
                savedEntity.getPdtId(),
                savedEntity.getPdtName(),
                savedEntity.getPdtPrice()
        );
    }

    /**
     * 찜 삭제 (예외 처리 포함)
     */
    public void removeWishlist(Integer pdtId,String email) {
        // pdtId와 email을 기준으로 위시리스트 항목이 존재하는지 확인
        Optional<WishlistEntity> wishlistItem = wishlistRepository.findByPdtIdAndEmail(pdtId, email);

        if (!wishlistItem.isPresent()) {
            throw new IllegalArgumentException("존재하지 않거나 이미 삭제된 찜 ID입니다.");
        }

        // 중복된 찜 항목이 있을 경우 삭제
        wishlistRepository.delete(wishlistItem.get());
    }

}
