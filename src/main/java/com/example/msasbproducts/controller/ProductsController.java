package com.example.msasbproducts.controller;

import com.example.msasbproducts.dto.ProductDetailDto;
import com.example.msasbproducts.dto.ProductReqDto;
import com.example.msasbproducts.dto.ShoppingCartReqDto;
import com.example.msasbproducts.kafka.TestKafProducer;
import com.example.msasbproducts.service.FileUpoadService;
import com.example.msasbproducts.service.ProductsService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private TestKafProducer testKafProducer;
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
    public ResponseEntity<String> registerProductWithImages(
            @RequestHeader("X-Auth-User") String email,
//            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            List<MultipartFile> images,
//            @RequestParam("productDetail") String productDetailJson
            String productDetailJson
            ) {  // JSON 데이터를 문자열로 받음
        try {
            // JSON String을 DTO 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("ObjectMapper read JSON...");
            System.out.println(productDetailJson);
            ProductDetailDto productDetailDto = objectMapper.readValue(productDetailJson, ProductDetailDto.class);
            System.out.println("ObjectMapper read");

            // 상품 등록 (이미지 포함)
            productsService.registerProductWithImages(email, productDetailDto, images);
            System.out.println("이미지 업로드 성공");

            // 상품 등록 후 Kafka 메시지 전송
            // Kafka 메시지를 전송하는 createPdt 메서드 호출
           String topic = "pdt-create"; // 예시 토픽 이름 (적절히 변경 필요)
           System.out.println("카프카 메세지 전송...");
           testKafProducer.createPdt(topic, productDetailDto);  // KafkaService에 전달
           System.out.println("카프카 메세지 전송완료");

            return ResponseEntity.ok("상품이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
//            e.printStackTrace();
            return ResponseEntity.status(500).body("상품 등록 실패: " + e.getMessage());
        }
    }



    // 상품 삭제
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer productId, @RequestHeader("X-Auth-User") String email) {
        try {
            // 상품 삭제 처리
            boolean isDeleted = productsService.deleteProductInfo(productId,email);

            if (isDeleted) {
                // Kafka 메시지 전송
                String topic = "pdt-delete";  // Kafka 토픽 설정
                ProductReqDto productReqDto = ProductReqDto.builder()
                        .email(email)
                        .ptId(productId)
                        .build();
                testKafProducer.deletePdt(topic, productReqDto);  // Kafka producer 호출

                return ResponseEntity.ok("상품이 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(404).body("상품을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("상품 삭제 실패");
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
