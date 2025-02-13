package com.example.msasbproducts.controller;

import com.example.msasbproducts.dto.ProductDetailDto;
import com.example.msasbproducts.dto.ShoppingCartReqDto;
import com.example.msasbproducts.dto.UploadDto;
import com.example.msasbproducts.kafka.KafkaProducer;
import com.example.msasbproducts.service.FileUpoadService;
import com.example.msasbproducts.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pdts")
public class ProductsController {

    @Autowired
    private ProductsService productsService;
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private FileUpoadService fileUploadService;  // 파일 업로드 서비스 추가

    // 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<org.example.msasbproducts.dto.ProductDto>> allProducts() {
        return ResponseEntity.ok(productsService.allProducts());
    }

    // 개별 상품 상세 조회
    @GetMapping("/detail/{pdtId}")
    public ResponseEntity<ProductDetailDto> productDetail(@PathVariable Integer pdtId) {
        return ResponseEntity.ok(productsService.getProductDetailInfo(pdtId));
    }

    // 상품등록
    @PostMapping("/register")
    public ResponseEntity<String> registerProduct(@RequestBody ProductDetailDto productDetailDto) {
        try {
            // 상품 등록 처리
            productsService.registerProductInfo(productDetailDto);

            // Kafka 메시지 전송
            kafkaProducer.sendMsg("msa-sb-products-register", productDetailDto.toString());

            return ResponseEntity.ok("상품 정보가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("상품 정보 등록 실패");
        }
    }

    @PostMapping("/uploadImages")
    public ResponseEntity<UploadDto> uploadImages(@RequestParam("images") List<MultipartFile> images) {
        try {
            // 이미지 파일을 S3에 업로드하고 URL 리스트 반환
            List<String> imageUrls = fileUploadService.submitFiles(images);

            // 반환할 DTO 생성
            UploadDto responseDto = UploadDto.builder()
                    .imageUrls(imageUrls)
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }



    // 장바구니 -> 제품 상세보기 -> 장바구니 담기
    @PostMapping("/shoppingcart")
    public ResponseEntity<String> addShoppingCart(
            @RequestHeader("X-Auth-User") String email,
            @RequestBody ShoppingCartReqDto shoppingCartReqDto) {
        productsService.addShoppingCart(email, shoppingCartReqDto);
        return ResponseEntity.ok("장바구니 추가 완료");
    }

    // 장바구니 전체 보기
    @GetMapping("/shoppingcart")
    public ResponseEntity<List<ShoppingCartReqDto>> getShoppingCart(@RequestHeader("X-Auth-User") String email) {
        return ResponseEntity.ok(productsService.getShoppingCart(email));
    }

    // 장바구니내 특정 제품 제거 (선택된 제품들 일괄 제거)
    @DeleteMapping("/shoppingcart")
    public ResponseEntity<String> removeItemsFromCart(
            @RequestHeader("X-Auth-User") String email,
            @RequestBody List<Integer> pdtId) {
        productsService.removeItemsFromCart(email, pdtId);
        return ResponseEntity.ok("선택한 제품이 장바구니에서 삭제되었습니다.");
    }

    // 장바구니내 특정 제품 수치 증/감
    @PostMapping("/shoppingcart/increase/{pdtId}")
    public ResponseEntity<String> increaseQuantity(
            @RequestHeader("X-Auth-User") String email,
            @PathVariable Integer pdtId) {
        productsService.updateCartQuantity(email, pdtId, 1);
        return ResponseEntity.ok("상품 수량이 증가했습니다.");
    }

    @PostMapping("/shoppingcart/decrease/{pdtId}")
    public ResponseEntity<String> decreaseQuantity(
            @RequestHeader("X-Auth-User") String email,
            @PathVariable Integer pdtId) {
        productsService.updateCartQuantity(email, pdtId, -1);
        return ResponseEntity.ok("상품 수량이 감소했습니다.");
    }
}
