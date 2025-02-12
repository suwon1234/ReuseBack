package com.example.msasbproducts.service;

import com.example.msasbproducts.dto.ProductDetailDto;
import org.example.msasbproducts.dto.ProductDto;
import com.example.msasbproducts.dto.ShoppingCartReqDto;
import com.example.msasbproducts.entity.CartEntity;
import com.example.msasbproducts.entity.ProductEntity;
import com.example.msasbproducts.repository.CartRepository;
import com.example.msasbproducts.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductsService {
    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private CartRepository cartRepository;



    public List<ProductDto> allProducts() {
        List<ProductEntity> pdts = productsRepository.findAll();
        return pdts.stream()
                .map(p -> ProductDto.builder()
                        .pdtName(p.getPdtName())
                        .pdtPrice(p.getPdtPrice())
                        .build())
                .collect(Collectors.toList());
    }

    public ProductDetailDto getProductDetailInfo(Integer pdtId) {
        ProductEntity productEntity = productsRepository.findById(pdtId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found or pdtId miss"));
        return ProductDetailDto.builder()
                .ptId(productEntity.getPdtId())
                .ptName(productEntity.getPdtName())
                .ptPrice(productEntity.getPdtPrice())
                .ptQuantity(productEntity.getPdtQuantity())
                .description(productEntity.getDescription())
                .imagePath(productEntity.getImagePath())
                .dtype(productEntity.getDtype())
                .build();
    }



    @Transactional
    public void addShoppingCart(String email, ShoppingCartReqDto shoppingCartReqDto) {
        Optional<ProductEntity> optionalProductEntity = productsRepository.findById(shoppingCartReqDto.getPdtId());
        if (optionalProductEntity.isPresent()) {
            ProductEntity productEntity = optionalProductEntity.get();
            if (shoppingCartReqDto.getPdtQuantity() > productEntity.getPdtQuantity()) {
                throw new IllegalArgumentException("재고량보다 더 많은 수량으로 장바구니에 담았습니다.");
            }
            Optional<CartEntity> opt = cartRepository.findByEmailAndPdtIdIn(email, Collections.singletonList(productEntity.getPdtId()));
            CartEntity cartEntity;
            if (opt.isPresent()) {
                cartEntity = opt.get();
                cartEntity.setQuantity(shoppingCartReqDto.getPdtQuantity() + cartEntity.getQuantity());
                cartEntity.setPrice(cartEntity.getQuantity() * productEntity.getPdtPrice());
            } else {
                cartEntity = CartEntity.builder()
                        .pdtId(productEntity.getPdtId())
                        .pdtName(productEntity.getPdtName())
                        .price(productEntity.getPdtPrice() * shoppingCartReqDto.getPdtQuantity())
                        .quantity(shoppingCartReqDto.getPdtQuantity())
                        .email(email)
                        .build();
            }
            cartRepository.save(cartEntity);
        } else {
            throw new IllegalArgumentException("Product not found or pdtId miss");
        }
    }

    public List<ShoppingCartReqDto> getShoppingCart(String email) {
        List<CartEntity> carts = cartRepository.findByEmail(email);
        return carts.stream()
                .map(cartEntity -> ShoppingCartReqDto.builder()
                        .pdtId(cartEntity.getPdtId())
                        .pdtName(cartEntity.getPdtName())
                        .pdtPrice(cartEntity.getPrice())
                        .pdtQuantity(cartEntity.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeItemsFromCart(String email, List<Integer> pdtId) {
        Optional<CartEntity> cartEntity = cartRepository.findByEmailAndPdtIdIn(email, pdtId);
        if (cartEntity.isPresent()) {
            cartRepository.delete(cartEntity.get());
        } else {
            throw new IllegalArgumentException("장바구니에 해당 상품이 없습니다.");
        }
    }



    @Transactional
    public void updateCartQuantity(String email, Integer pdtId, int newQuantity) {
        Optional<CartEntity> cartEntityOpt = cartRepository.findByEmailAndPdtIdIn(email, Collections.singletonList(pdtId));
        if (cartEntityOpt.isPresent()) {
            CartEntity cartEntity = cartEntityOpt.get();
            if (newQuantity <= 0) {
                cartRepository.delete(cartEntity);
            } else {
                Optional<ProductEntity> productEntityOpt = productsRepository.findById(pdtId);
                if (productEntityOpt.isPresent() && newQuantity <= productEntityOpt.get().getPdtQuantity()) {
                    cartEntity.setQuantity(newQuantity);
                    cartEntity.setPrice(newQuantity * productEntityOpt.get().getPdtPrice());
                    cartRepository.save(cartEntity);
                } else {
                    throw new IllegalArgumentException("재고 부족 또는 상품 없음");
                }
            }
        } else {
            throw new IllegalArgumentException("장바구니에 해당 상품이 없습니다.");
        }


    }

    @Transactional
    public void registerProduct(ProductDetailDto productDetailDto) {
        ProductEntity productEntity = ProductEntity.builder()
                .pdtName(productDetailDto.getPdtName()) // 상품명
                .pdtPrice(productDetailDto.getPdtPrice()) // 상품 가격
                .pdtQuantity(productDetailDto.getPdtQuantity()) // 상품 수량
                .description(productDetailDto.getDescription()) // 상품 설명
                .imagePath(productDetailDto.getImagePath()) // 상품 이미지 경로
                .dtype(productDetailDto.getDtype()) // 상품 종류
                .email(productDetailDto.getEmail()) // 등록한 사람의 email
                .build();

        productsRepository.save(productEntity);
    }
}
