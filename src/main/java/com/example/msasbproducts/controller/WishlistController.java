package com.example.msasbproducts.controller;

import com.example.msasbproducts.dto.WishlistDto;
import com.example.msasbproducts.entity.WishlistEntity;
import com.example.msasbproducts.kafka.KafkaProducer;
import com.example.msasbproducts.kafka.TestKafProducer;
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

    @Autowired
    private TestKafProducer testKafProducer;

    // 특정 이메일의 위시리스트 조회
    @GetMapping("/{email}")
    public ResponseEntity<List<WishlistDto>> getWishlist(@PathVariable String email) {
        List<WishlistDto> wishlist = wishlistService.getWishlist(email);  // WishlistService가 List<WishlistEntity>를 반환한다고 가정
        return ResponseEntity.ok(wishlist);
    }

    // 위시리스트에 항목 추가
    @PostMapping
    public ResponseEntity<WishlistDto> addWishlist(@RequestBody WishlistEntity wishlistEntity) {
        try {
            WishlistDto addedWishlist = wishlistService.addWishlist(wishlistEntity);

            // Kafka 메시지 전송
            String topic = "wish-pdt";  // 적절한 Kafka 토픽 설정
            testKafProducer.wishPdt(topic, addedWishlist);  // Kafka producer 호출

            return ResponseEntity.status(201).body(addedWishlist);  // 201 Created 상태 코드
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // 서버 오류 발생 시 500 상태 코드
        }
    }

    // 위시리스트에서 항목 제거
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeWishlist(@PathVariable Integer id,
                                               @RequestHeader("X-Auth-User") String email) {  // 이메일을 헤더에서 받음
        try {
            wishlistService.removeWishlist(id);

            // Kafka 메시지 전송
            String topic = "wish-pdt-delete";  // 적절한 Kafka 토픽 설정
            testKafProducer.wishPdtDelete(topic, id, email);  // 상품 ID와 이메일을 Kafka producer에 전달

            return ResponseEntity.noContent().build();  // 204 No Content 상태 코드
        } catch (Exception e) {
            return ResponseEntity.status(500).build();  // 서버 오류 발생 시 500 상태 코드
        }
    }
}
