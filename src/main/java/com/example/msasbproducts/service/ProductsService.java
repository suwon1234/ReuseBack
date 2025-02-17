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
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
//ㅁㄴㅇㅁㅁ
@Service
public class ProductsService {
    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private FileUpoadService fileUploadService;


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
    public void registerProductWithImages(String email, ProductDetailDto productDetailDto, List<MultipartFile> images) {
        try {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("이메일 정보가 필요합니다.");
            }

            System.out.println("제품 정보 생성중...");
            ProductEntity productEntity = ProductEntity.builder()
                    .pdtName(productDetailDto.getPdtName())
                    .pdtPrice(productDetailDto.getPdtPrice())
                    .pdtQuantity(productDetailDto.getPdtQuantity())
                    .description(productDetailDto.getDescription())
                    .dtype(productDetailDto.getDtype())
                    .imageUrls(productDetailDto.getImageUrls())
                    .email(email)
                    .build();
            System.out.println(productEntity.toString());
            System.out.println("제품 정보 생성 완료...");

            System.out.println("제품 정보 저장중...");
            productEntity = productsRepository.save(productEntity); // ✅ 상품을 먼저 저장하고 pdtId 생성
            Long pdtId = Long.valueOf(productEntity.getPdtId());
            System.out.println(pdtId);
            System.out.println("제품 정보 저장 완료");
            productDetailDto.setPdtId(Integer.valueOf(pdtId.toString()));


            if (pdtId == null) {
                throw new RuntimeException("상품 등록 실패: pdtId가 생성되지 않았습니다.");
            }

            System.out.println("이미지 주소 정보 저장중...");
            List<String> imageUrls = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                imageUrls = fileUploadService.submitFiles(images);
            }
            System.out.println("이미지 주소 정보 저장완료");

            System.out.println("Service : 제품 정보 등록 완료!");

        } catch (Exception e) {
            throw new RuntimeException("상품 등록 중 오류 발생: " + e.getMessage());
        }
    }

    // 미사용
//    @Transactional
//    public void registerProductWithImages(String email, ProductDetailDto productDetailDto,  List<MultipartFile> images) {
//        try {
//
//            if (email == null || email.isEmpty()) {
//                throw new IllegalArgumentException("이메일 정보가 필요합니다.");
//            }
//
//            // 이미지 업로드 처리 (S3 또는 다른 저장소 사용)
//            System.out.println("Service: 업로드중...");
//            List<String> imageUrls = new ArrayList<>();
//            if (images != null && !images.isEmpty()) {
//                imageUrls = fileUploadService.submitFiles(images);
//            }
//            System.out.println("Service: 업로드 성공");
//
//            // 상품 엔티티 생성
//            System.out.println("Service: 상품 엔티티 생성중...");
//            ProductEntity productEntity = ProductEntity.builder()
//                    .pdtName(productDetailDto.getPdtName())
//                    .pdtPrice(productDetailDto.getPdtPrice())
//                    .pdtQuantity(productDetailDto.getPdtQuantity())
//                    .description(productDetailDto.getDescription())
//                    .dtype(productDetailDto.getDtype())
//                    .email(email)
//                    .imageUrls(imageUrls)  // 업로드된 이미지 URL 저장 (ProductEntity에 @ElementCollection 추가 필요)
//                    .build();
//            System.out.println("Service: 상품 엔티티 생성완료, DB 저장 시도");
//            // 상품 정보 DB 저장
//            System.out.println(productEntity.toString());
//
//            productsRepository.save(productEntity);
//            System.out.println("Service: 상품 엔티티 저장 완료");
//
//            // 이미지 업로드 정보 저장
//            System.out.println("Serivce: 이미지 정보 저장...");
//            System.out.println(imageUrls);
//            for (String imageUrl : imageUrls) {
//                UploadEntity uploadEntity = new UploadEntity(email, Collections.singletonList(imageUrl));
//                uploadRepository.save(uploadEntity);
//                System.out.println(uploadRepository.findByEmail("a@a.com"));
//            }
//            System.out.println("Serivce: 이미지 정보 저장 완료");
//
//        } catch (Exception e) {
//            throw new RuntimeException("상품 등록 중 오류 발생: " + e.getMessage());
//        }
//    }




    @Transactional
    public boolean deleteProductInfo(Integer productId, String email) {
        try {
            // 상품 조회
            Optional<ProductEntity> optionalProductEntity = productsRepository.findById(productId);

            if (optionalProductEntity.isPresent()) {
                ProductEntity productEntity = optionalProductEntity.get();

                // 상품이 특정 사용자에게 속한 것인지 확인 (여기서 이메일을 체크)
                if (productEntity.getEmail().equals(email)) {
                    productsRepository.deleteById(productId);  // 상품 삭제
                    return true;
                } else {
                    throw new RuntimeException("이메일이 일치하지 않아 삭제할 수 없습니다.");
                }
            } else {
                return false; // 상품이 존재하지 않으면 삭제하지 않음
            }
        } catch (Exception e) {
            throw new RuntimeException("상품 삭제 실패: " + e.getMessage());
        }
    }


}
