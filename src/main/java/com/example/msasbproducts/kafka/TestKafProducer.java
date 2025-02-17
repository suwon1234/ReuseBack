package com.example.msasbproducts.kafka;

import com.example.msasbproducts.dto.ProductDetailDto;
import com.example.msasbproducts.dto.ProductReqDto;
import com.example.msasbproducts.dto.WishlistDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestKafProducer {
    private static final Logger logger = LoggerFactory.getLogger(TestKafProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void createPdt(String topic, ProductDetailDto productDetailDto) throws JsonProcessingException {
        logger.info("상품 등록 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(SendPdtDto.builder()
                        .email(productDetailDto.getEmail())
                        .ptId(productDetailDto.getPdtId())
                        .pdtName(productDetailDto.getPdtName())
                        .price(Float.valueOf(productDetailDto.getPdtPrice()))
                        .imageUrl(productDetailDto.getImageUrls())
                        .build()));
        logger.info("상품 등록 메세지 전송 완료");
    }

    public void deletePdt(String topic, ProductReqDto productReqDto) throws JsonProcessingException {
        logger.info("상품 삭제 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(ProductReqDto.builder()

                        .email(productReqDto.getEmail())
                        .ptId(productReqDto.getPdtId())
                .build()));
        logger.info("상품 삭제 메세지 전송 완료");
    }

    public void wishPdt(String topic, WishlistDto wishlistDto) throws JsonProcessingException {
        logger.info("찜 등록 메세지 전송");
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(WishlistDto.builder()

                        .pdtId(wishlistDto.getPdtId())
                        .email(wishlistDto.getEmail())
                .build()));
        logger.info("찜 등록 메세지 전송 완료");
    }

    public void wishPdtDelete(String topic, Integer pdtId, String email) throws JsonProcessingException {
        logger.info("찜 삭제 메세지 전송");

        // 삭제할 상품 ID와 이메일을 포함한 데이터를 Kafka에 전송
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(WishlistDto.builder()
                .pdtId(pdtId)
                .email(email)
                .build()));

        logger.info("찜 삭제 메세지 전송 완료");
    }


}
